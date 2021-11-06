package me.yangxiaobin.aspectandroid

import kotlinx.coroutines.*
import me.yangxiaobin.lib.coroutine.coroutineHandler
import me.yangxiaobin.lib.ext.*
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.util.*

@CacheableTask
open class AjcCompileTask : DefaultTask() {

    private val tag = "AjcCompileTask"
    private val logger = Logger.copy().setLevel(LogLevel.VERBOSE)
    private val logV = logger.log(LogLevel.VERBOSE, tag)
    private val logI = logger.log(LogLevel.INFO, tag)
    private val logD = logger.log(LogLevel.DEBUG, tag)
    private val logE = logger.log(LogLevel.ERROR, tag)

    private val mLogger by lazy { this.project.logger }

    private val ajcScope: CoroutineScope by lazy {
        CoroutineScope(
//            transformExecutor.asCoroutineDispatcher()
            Dispatchers.IO
                    + SupervisorJob()
                    + coroutineHandler
                    + CoroutineName("Transport-Coroutine")
        )
    }

    private val messageHandler by lazy { MessageHandler(false) }
    private val bootclasspath: String by lazy { this.project.getAppExtension?.bootClasspath.toPath() }


    @Input
    var variantName: String? = null

    // Use ABSOLUTE to be safe, the API that this method calls is deprecated anyway.
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

        val dirs: List<File>? = inputDirectory.listFiles()?.filter { it.isDirectory }
        val jars: List<File>? = inputDirectory.listFiles()?.filter { it.isJarFile() }

        logD("Ajc input dirs size :${dirs?.size}, jars size :${jars?.size}")


        if (!dirs.isNullOrEmpty()) ajcCompileDir(dirs)
        if (!jars.isNullOrEmpty()) ajcCompileJars(jars)


        logI("AjcCompile ends in ${(System.currentTimeMillis() - t1).toFormat(false)}.")
    }

    private fun ajcCompileDir(dirs: List<File>) {

        val compileClasspathFiles = calculateCompileClasspathFiles()
        val compileClasspath: String = compileClasspathFiles.toPath()

        // TODO  uncomment
        //logV("ajcCompileDir compileClasspath :$compileClasspath")

        // Write compile classpath into temp file
        if (shouldDumpToFile()) {
            val dumpString =
                "size : ${compileClasspathFiles.size}\r\n" + compileClasspathFiles.joinToString(separator = "\r\n") { it.absolutePath }

            File(this.project.buildDir, "ajcTmp/ajc-compile-classpath.txt").touch().writeText(dumpString)
        }

        dirs.forEach { dir: File ->
            ajcScope.launch {

                val args = arrayOf<String>(
                    "-1.8",
                    "-showWeaveInfo",
                    "-d", dir.absolutePath,
                    "-inpath", dir.absolutePath,
                    //"-aspectpath", aspectpath,
                    "-classpath", compileClasspath,
                    "-bootclasspath", bootclasspath,
                )

                logV("""
                    cur : ${dir.absolutePath}
                    aspectj args : ${args.contentToString()}
                """.trimIndent())

                //Main().run(args, messageHandler)

                handleWeaveMessage(dir)
            }
        }
    }

    private fun ajcCompileJars(dirs: List<File>) {

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

    private fun shouldDumpToFile(): Boolean =
        this.project.extensions.findByType(AspectAndroidExt::class.java)?.generateDebugLogFile == true

    private fun handleWeaveMessage(cur: File) {

        messageHandler.clearMessages()

        val errorFile by lazy { File(this.project.buildDir, "ajcTmp/weave-err.txt").touch() }
        val warnFile by lazy { File(this.project.buildDir, "ajcTmp/weave-warn.txt").touch() }
        val otherFile by lazy { File(this.project.buildDir, "ajcTmp/weave-other.txt").touch() }

        for (message: IMessage in messageHandler.getMessages(null, true)) {
            val msg by lazy {
                """
                cur : ${cur.absolutePath}
                ${message.kind} /  ${message.message}
            """.trimIndent()
            }

            when (message.kind) {
                IMessage.ABORT, IMessage.ERROR, IMessage.FAIL -> {
                    message.thrown?.printStackTrace()
                    //logE(msg)
                    mLogger.error(msg)

                    if (shouldDumpToFile()) errorFile.appendText(msg + "\r\n")
                }

                IMessage.WARNING, IMessage.INFO, IMessage.DEBUG -> {
                    //logD(msg)
                    mLogger.debug(msg)

                    if (shouldDumpToFile()) warnFile.appendText(msg + "\r\n")
                }

                else -> {
                    //logI(msg)
                    mLogger.info(msg)

                    if (shouldDumpToFile()) otherFile.appendText(msg + "\r\n")

                }
            }
        }

    }
}
