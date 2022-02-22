package me.yangxiaobin.aspectandroid

import com.android.build.gradle.api.ApplicationVariant
import me.yangxiaobin.lib.ext.*
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.util.*

@CacheableTask
open class AjcCompileTask : DefaultTask() {

    private val tag = "AjcCompileTask"
    private val logger = Logger.copy().setLevel(LogLevel.INFO)
    private val logV = logger.log(LogLevel.VERBOSE, tag)
    private val logI = logger.log(LogLevel.INFO, tag)
    private val logD = logger.log(LogLevel.DEBUG, tag)
    private val logE = logger.log(LogLevel.ERROR, tag)

    private val mLogger by lazy { this.project.logger }

    private val messageHandler by lazy { MessageHandler(false) }
    private val bootclasspath: String by lazy { this.project.getAppExtension?.bootClasspath.toPath() }

    @Input
    var variantName: String? = null

    @PathSensitive(PathSensitivity.ABSOLUTE)
    @InputDirectory
    var inputDir: File? = null

    @OutputDirectory
    var outputDir: File? = null

    @TaskAction
    fun ajcCompile() {
        val t1 = System.currentTimeMillis()
        logI("AjcCompile start.")

        val inputDirectory = requireNotNull(inputDir) { return }

        /**
         * NB. Only consider two situations,dir and jar.
         */
        val dirs: List<File>? = inputDirectory.listFiles()?.filter { it.isDirectory }
        val jars: List<File>? = inputDirectory.listFiles()?.filter { it.isJarFile() }

        logD("Ajc input dirs size :${dirs?.size}, jars size :${jars?.size}")

        cleanup()

        if (!dirs.isNullOrEmpty()) ajcCompileDirs(dirs)
        if (!jars.isNullOrEmpty()) ajcCompileJars(jars)


        logI("AjcCompile ends in ${(System.currentTimeMillis() - t1).toFormat(false)}.")
    }

    private fun cleanup() {
        File(this.project.buildDir, "ajcTmp").deleteRecursively()
    }

    private fun ajcCompileDirs(dirs: List<File>) {

        val compileClasspathFiles = calculateCompileClasspathFiles()
        val compileClasspath: String = compileClasspathFiles.toPath()

        // Write compile classpath into temp file.
        if (shouldDumpToFile()) {
            val dumpString =
                "size : ${compileClasspathFiles.size}\r\n" + compileClasspathFiles.joinToString(separator = "\r\n") { it.absolutePath }

            File(this.project.buildDir, "ajcTmp/ajc-compile-classpath.txt").touch().writeText(dumpString)
        }

        val weaveDirActions: List<() -> Unit> = dirs.map { dir: File ->
            {
                val args = arrayOf<String>(
                    "-1.8",
                    "-showWeaveInfo",
                    "-d", dir.absolutePath,
                    "-inpath", dir.absolutePath,
                    "-aspectpath", dir.parent,
                    "-classpath", compileClasspath,
                    "-bootclasspath", bootclasspath,
                )

                logV(
                    """
                    cur : ${dir.absolutePath}
                    aspectj args : ${args.contentToString()}
                """.trimIndent()
                )

                messageHandler.clearMessages()

                Main().run(args, messageHandler)

                if (!handleWeaveMessage(dir)) logE("Weave failed f : ${dir.absolutePath}")
            }
        }

        val t1 = System.currentTimeMillis()
        logI("async ajc compile dirs begins.")

        weaveDirActions.forEach { it.invoke() }
        logI("async ajc compile DIRS ends in ${(System.currentTimeMillis() - t1).toFormat(false)}.")
    }

    /**
     * Cause ajc can NOT output the jarfile with same name with the original one.
     *
     * 1. Rename original jar.
     * 2. Do ajc compilation.
     * 3. Rename back.
     */
    private fun ajcCompileJars(jars: List<File>) {

        if (this.project.extensions.findByType(AspectAndroidExt::class.java)?.supportTransitiveJars == false) return

        val t1 = System.currentTimeMillis()
        logI("async ajc compile jars begins.")


        val sourceCodeCompileClasspathFiles = calculateCompileClasspathFiles()
        val jarCompileClasspathFiles = calculateDependencyJarsCompileClasspath()
        val compileJarClasspath = (sourceCodeCompileClasspathFiles + jarCompileClasspathFiles).toPath()

        // Write jar compile classpath into temp file.
        if (shouldDumpToFile()) {
            val dumpString =
                "size : ${jarCompileClasspathFiles.size}\r\n" + jarCompileClasspathFiles.joinToString(separator = "\r\n") { it.absolutePath }
            File(this.project.buildDir, "ajcTmp/ajc-jar-classpath.txt").touch().writeText(dumpString)
        }

        val prefix = "pre-ajc-"

        val weaveJarActions: List<() -> Unit> = jars
            .map { it.renamed("$prefix${it.name}") }
            .map { preJar: File ->
                {
                    val originalName = preJar.parent + File.separator + preJar.name.substring(prefix.length)

                    val args = arrayOf<String>(
                        "-1.8",
                        "-showWeaveInfo",
                        "-d", preJar.parent,
                        "-inpath", preJar.absolutePath,
                        "-aspectpath", preJar.parent,
                        "-outjar", originalName,
                        "-classpath", compileJarClasspath,
                        "-bootclasspath", bootclasspath,
                    )

                    logV(
                        """
                    cur : ${preJar.absolutePath}
                    aspectj args : ${args.contentToString()}
                """.trimIndent()
                    )

                    messageHandler.clearMessages()

                    Main().run(args, messageHandler)

                    if (!handleWeaveMessage(preJar)) {
                        logE("Weave failed jar : ${preJar.absolutePath}")
                        preJar.renamed(preJar.name.substring(prefix.length))
                    } else {
                        preJar.delete()
                        logD("Weave successful jar : ${preJar.name}")
                    }
                }
            }

        weaveJarActions.forEach { it.invoke() }
        logI("async ajc compile JARS ends in ${(System.currentTimeMillis() - t1).toFormat(false)}.")
    }

    private fun calculateCompileClasspathFiles(): Set<File> {

        val variantName = variantName ?: return emptySet()

        // compileAppstoreDebugKotlin
        val kotlinCompilePath: Set<File> = this.project.tasks
            .filterIsInstance<KotlinCompile>()
            .find { it.name == "compile${variantName.capitalize(Locale.getDefault())}Kotlin" }
            ?.classpath
            ?.toSet()
            ?: emptySet()

        // compileAppstoreDebugJavaWithJavac
        val javaCompilePath: Set<File> = this.project.tasks
            .filterIsInstance<JavaCompile>()
            .find { it.name == "compile${variantName.capitalize(Locale.getDefault())}JavaWithJavac" }
            ?.classpath
            ?.toSet()
            ?: emptySet()

        return javaCompilePath union kotlinCompilePath
    }

    /**
     * TODO To be More precise.
     */
    private fun calculateDependencyJarsCompileClasspath(): Set<File> {

        val curVariant: ApplicationVariant = this.project.getAppExtension
            ?.applicationVariants
            ?.find { it.name == variantName }
            ?: return emptySet()

        val variantRtCp: Configuration = curVariant.runtimeConfiguration

        val artifactType: Attribute<String> = Attribute.of("artifactType", String::class.java)
        // Specific artifactType attribute to avoid variantSelectionFailure.
        val fs: Set<File> = variantRtCp.incoming.artifactView { viewConfig: ArtifactView.ViewConfiguration ->
            viewConfig.attributes {
                // or jar
                it.attribute(artifactType, "android-classes-jar")
                it.attribute(artifactType, "jar")
            }
        }.artifacts.artifactFiles.toSet()

        return fs
    }

    private fun shouldDumpToFile(): Boolean =
        this.project.extensions.findByType(AspectAndroidExt::class.java)?.generateDebugLogFile == true


    /**
     * @return Whether weave successful or not.
     */
    private fun handleWeaveMessage(cur: File): Boolean {

        var weaveSuccessful = true

        val errorFile by lazy { File(this.project.buildDir, "ajcTmp/weave-err.txt").touch() }
        val warnFile by lazy { File(this.project.buildDir, "ajcTmp/weave-warn.txt").touch() }
        val otherFile by lazy { File(this.project.buildDir, "ajcTmp/weave-other.txt").touch() }

        for (message: IMessage in messageHandler.getMessages(null, true)) {
            val msg by lazy {
                """
cur file : ${cur.absolutePath}
message kind : ${message.kind} / message : ${message.message}
            """.trimIndent()
            }

            when (message.kind) {
                IMessage.ABORT, IMessage.ERROR, IMessage.FAIL -> {
                    weaveSuccessful = false
                    message.thrown?.printStackTrace()
                    mLogger.error(msg)

                    if (shouldDumpToFile()) errorFile.appendText(
                        """
$msg
${message.thrown?.stackTraceToString()} 
                    """.trimIndent()
                    )
                }

                IMessage.WARNING, IMessage.INFO, IMessage.DEBUG -> {
                    logV(msg)

                    if (shouldDumpToFile()) warnFile.appendText(msg + "\r\n")
                }

                else -> {
                    logV(msg)

                    if (shouldDumpToFile()) otherFile.appendText(msg + "\r\n")

                }
            }
        }

        return weaveSuccessful
    }
}
