package me.yangxiaobin.aspectandroid

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.pipeline.TransformTask
import me.yangxiaobin.lib.base.BasePlugin
import me.yangxiaobin.lib.ext.*
import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.transform.AbsLegacyTransform
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.*
import org.gradle.api.attributes.Attribute
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

class AspectAndroidPlugin : BasePlugin() {

    override val TAG: String get() = "AAP"

    private val messageHandler by lazy { MessageHandler(false) }

    private var curVariantName: String? = null

    private val ext: AspectAndroidExt by lazy { mProject.extensions.getByType(AspectAndroidExt::class.java) }

    private val buildLogFile by lazy { ext.generateDebugLogFile }

    override val myLogger: ILog
        get() = super.myLogger.copy().setLevel(LogLevel.INFO)

    private var hasWroteClasspath = false
    private val classpathFile by lazy { File(mProject.buildDir, "ajcTmp/ajc-classpath.txt").touch() }

    private val logEFile by lazy { File(mProject.buildDir, "ajcTmp/ajc-logE.txt").touch() }
    private val logIFile by lazy { File(mProject.buildDir, "ajcTmp/ajc-logI.txt").touch() }
    private val logElseFile by lazy { File(mProject.buildDir, "ajcTmp/ajc-logElse.txt").touch() }

    override fun apply(p: Project) {
        super.apply(p)

        logI("${p.name} applied AspectAndroidPlugin.")

        cleanup()

        mProject.extensions.create("aspectAndroid", AspectAndroidExt::class.java)

        val aspectTransform = AspectTransform(mProject)

        configAjcClasspath()

        p.afterEvaluate {
            logI("Resolved aspectJrt version :${ext.aspectJrtVersion}")
            p.getAppExtension?.registerTransform(aspectTransform)
            p.dependencies.add("implementation", "org.aspectj:aspectjrt:${ext.aspectJrtVersion}")
        }

        configAjcCompileTask(aspectTransform.name)
    }

    private fun cleanup() {
        logEFile.delete()
        logIFile.delete()
        logElseFile.delete()
    }

    private fun configAjcClasspath() {
        mProject.configurations.create("ajc")

        mProject.dependencies.add("ajc", "org.aspectj:aspectjrt:${ext.aspectJrtVersion}")
    }


    private fun configAjcCompileTask(transformName: String) {

        mProject.gradle.taskGraph.whenReady { graph: TaskExecutionGraph ->

            graph.allTasks
                .find { it.name.contains(transformName, ignoreCase = true) }
                ?.doLast { t ->

                    curVariantName = ((t as? TransformTask)?.transform as? AbsLegacyTransform)?.currentVariantName
                        ?: kotlin.run {
                            logE("Can NOT get current variant name, so ajc compilation returned.")
                            return@doLast
                        }

                    logI("${t.name} do last begins, current variant name: $curVariantName, output size :${t.outputs.files.asFileTree.files.size} >>")

                    doAjcCompilation(t)

                    logI("${t.name} do last ends, current variant name: $curVariantName, output size :${t.outputs.files.asFileTree.files.size} >>")
                }

        }
    }

    private fun doAjcCompilation(t: Task) {

        val transformOutputPath = t.outputs.files.asPath
        val transformOutputFile = File(transformOutputPath)

        val dirs: List<File>? = transformOutputFile.listFiles()?.filter { it.isDirectory }
        val jars: List<File>? = transformOutputFile.listFiles()?.filter { it.isJarFile() }

        if (!dirs.isNullOrEmpty()) ajcCompileDir(dirs)
        if (!jars.isNullOrEmpty()) ajcCompileJars(jars)
    }

    private fun ajcCompileDir(dirs: List<File>) {

        val t1 = System.currentTimeMillis()
        logI("  ajcCompileDir begins.")

        val aspectpath = dirs.joinToString(separator = File.pathSeparator)

        dirs.forEach { dir ->

            val destDir: String = dir.absolutePath

            val classpath: String = calculateClasspath()

            val bootclasspath: String = (mProject.getAppExtension?.bootClasspath ?: return).toPath()

            val args = arrayOf<String>(
                "-1.8",
                "-showWeaveInfo",
                "-d", destDir,
                "-inpath", destDir,
                "-aspectpath", aspectpath,
                "-classpath", classpath,
                "-bootclasspath", bootclasspath,
            )

            logV("  ajcCompileDir args :${args.contentToString()}")

            Main().run(args, messageHandler)

            populateMessageHandler(dir)
        }

        logI("  ajcCompileDir ends in ${(System.currentTimeMillis() - t1).toFormat(false)}.")
    }

    /**
     * Cause ajc can NOT output the jarfile with same name with the original one.
     *
     * 1. Rename original jar.
     * 2. Do ajc compilation.
     * 3. Rename back.
     */
    private fun ajcCompileJars(jars: List<File>) {

        val t1 = System.currentTimeMillis()
        logI("  ajcCompileJars begins")


        val configNames = mProject.configurations.filter { it.isCanBeResolved }.joinToString("\r\n") { it.name }
        logV("current variant : $curVariantName, config names :$configNames")

        val prefix = "pre-ajc-"

        jars
            // 1. Rename
            .map { it.renamed("$prefix${it.name}") }
            // 2. Ajc compile
            .forEach { preJar ->

                val originalName = preJar.parent + File.separator + preJar.name.substring(prefix.length)

                val jarInpath = preJar.absolutePath

                val destDir = preJar.parent

                val classpath: String = calculateClasspath()

                val bootclasspath: String = (mProject.getAppExtension?.bootClasspath ?: return).toPath()

                val args = arrayOf<String>(
                    "-1.8",
                    "-showWeaveInfo",
                    "-d", destDir,
                    "-inpath", jarInpath,
                    "-aspectpath", destDir,
                    "-classpath", classpath,
                    "-outjar", originalName,
                    "-bootclasspath", bootclasspath,
                )

                logI("  process jarfile :${preJar.name}")
                logV("  ajcCompileJars args :${args.contentToString()}")

                Main().run(args, messageHandler)

                val isSuccessful = populateMessageHandler(preJar)

                // 3. Delete original jars.
                if (isSuccessful) preJar.delete() else preJar.renamed(preJar.name.substring(prefix.length))
            }

        logI("  ajcCompileJars ends in ${(System.currentTimeMillis() - t1).toFormat(false)}")
    }

    private fun calculateClasspath(): String {

        val curVariantName = curVariantName ?: throw IllegalArgumentException("current variant name can NOT be null.")

        val appExt = mProject.getAppExtension
            ?: throw IllegalArgumentException("Aspect-Android plugin can ONLY be applied on Android project.")

        val variant: ApplicationVariant = appExt.applicationVariants.matching { v: ApplicationVariant ->
            v.name.contains(curVariantName, ignoreCase = true)
        }.singleOrNull() ?: throw IllegalArgumentException("This build does NOT contains $curVariantName variant.")


        val variantRtCp: Configuration = variant.runtimeConfiguration

        val artifactType: Attribute<String> = Attribute.of("artifactType", String::class.java)
        // Specific artifactType attribute to avoid variantSelectionFailure.
        val fs = variantRtCp.incoming.artifactView { viewConfig: ArtifactView.ViewConfiguration ->
            viewConfig.attributes {
                // or jar
                it.attribute(artifactType, "android-classes-jar")
                it.attribute(artifactType, "jar")
            }
        }.artifacts.artifactFiles.toSet()


        val ajcJrtClasspath: String = mProject.configurations.getByName("ajc").asPath

        val kotlinCompilePath: Set<File> =
            (mProject.tasks.find { it is KotlinCompile } as? KotlinCompile)?.classpath?.toSet() ?: emptySet()

        val javaCompilePath: Set<File> =
            (mProject.tasks.find { it is JavaCompile } as? JavaCompile)?.classpath?.toSet() ?: emptySet()


        val combinedClasspath: Set<File> = kotlinCompilePath + javaCompilePath + fs

        val path = combinedClasspath
            .filterNot { it.name.endsWith(".aar") }
            .toPath()

        return "$path:$ajcJrtClasspath"
            .also { cp ->
                if (!buildLogFile || hasWroteClasspath) return@also

                // Write whole classpath into file.
                val cpString = cp
                    .split(File.pathSeparator)
                    .joinToString("\r\n") { singlePath -> singlePath.split(File.separator).last() }

                classpathFile.writeText(cpString)
                hasWroteClasspath = true

                logI("Has wrote class into file : ${classpathFile.absolutePath}")
            }
    }


    /**
     * @return AJC result, true for success.
     */
    private fun populateMessageHandler(cur: File): Boolean {

        var isSuccessful = true

        messageHandler.clearMessages()

        for (message: IMessage in messageHandler.getMessages(null, true)) {
            val msg by lazy { """
                cur file : ${cur.name}
                ${message.kind} /  ${message.message}
            """.trimIndent() }

            when (message.kind) {
                IMessage.ABORT, IMessage.ERROR, IMessage.FAIL -> {
                    message.thrown?.printStackTrace()
                    //logE(message.message)

                    mLogger.error(msg)
                    isSuccessful = false

                    if (!buildLogFile) return isSuccessful
                    logEFile.appendText(msg + "\r\n")

                }
                IMessage.WARNING, IMessage.INFO, IMessage.DEBUG -> {
                    //logD(msg)
                    mLogger.debug(msg)

                    if (!buildLogFile) return isSuccessful
                    logIFile.appendText(msg + "\r\n")
                }
                else -> {
                    //logI(msg)
                    mLogger.info(msg)

                    if (!buildLogFile) return isSuccessful
                    logElseFile.appendText(msg + "\r\n")

                }
            }
        }

        return isSuccessful
    }
}
