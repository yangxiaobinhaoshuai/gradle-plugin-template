package me.yangxiaobin.aspectandroid

import com.android.build.gradle.internal.pipeline.TransformTask
import me.yangxiaobin.lib.base.BasePlugin
import me.yangxiaobin.lib.ext.getAppExtension
import me.yangxiaobin.lib.ext.isJarFile
import me.yangxiaobin.lib.ext.toFormat
import me.yangxiaobin.lib.ext.toPath
import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import java.io.File

class AspectAndroidPlugin : BasePlugin() {

    override val TAG: String get() = "AAP"

    private val messageHandler by lazy { getLogMessageHandler() }

    override val myLogger: ILog
        get() = super.myLogger.copy().setLevel(LogLevel.VERBOSE)

    override fun apply(p: Project) {
        super.apply(p)

        logI.invoke("${p.name} applied AspectAndroidPlugin.")

        configAjcClasspath()

        val aspectTransform = AspectTransform(mProject)

        p.afterEvaluate { p.getAppExtension?.registerTransform(aspectTransform) }

        configAjcCompileTask(aspectTransform.name)
    }

    private fun configAjcClasspath() {
        mProject.configurations.create("ajc")

        mProject.dependencies.add("ajc", "org.aspectj:aspectjrt:1.9.7")
    }


    private fun configAjcCompileTask(transformName: String) {

        mProject.gradle.taskGraph.whenReady { graph: TaskExecutionGraph ->

            graph.allTasks
                .find { it.name.contains(transformName, ignoreCase = true) }
                ?.doLast { t ->

                    logI("${t.name} do last begins. >>")

                    doAjcCompilation(t as TransformTask)
                }
        }
    }

    private fun doAjcCompilation(transform: TransformTask) {

        val transformOutputPath = transform.outputs.files.asPath
        val transformOutputFile = File(transformOutputPath)

        val dirs: List<File>? = transformOutputFile.listFiles()?.filter { it.isDirectory }
        val jars: List<File>? = transformOutputFile.listFiles()?.filter { it.isJarFile() }

        if (!dirs.isNullOrEmpty()) ajcCompileDir(dirs)
        if (!jars.isNullOrEmpty()) ajcCompileJars(jars, transformOutputPath)
    }

    private fun ajcCompileDir(dirs: List<File>) {

        val t1 = System.currentTimeMillis()
        logI("  ajcCompileDir begins.")

        val aspectpath = dirs.joinToString(separator = File.pathSeparator)

        dirs.forEach { dir ->

            val destDir: String = dir.absolutePath

            val ajcJrtClasspath = mProject.configurations.getByName("ajc").asPath

            val bootclasspath: String = (mProject.getAppExtension?.bootClasspath ?: return).toPath()

            val args = arrayOf<String>(
                "-1.8",
                "-showWeaveInfo",
                "-d", destDir,
                "-inpath", destDir,
                "-aspectpath", aspectpath,
                "-classpath", ajcJrtClasspath,
                "-bootclasspath", bootclasspath,
            )

            logV("  ajcCompileDir args :${args.contentToString()}")

            Main().run(args, messageHandler)
        }

        logI("  ajcCompileDir ends in ${(System.currentTimeMillis() - t1).toFormat(false)}.")
    }

    // TODO 验证 jars weave 是否正确
    private fun ajcCompileJars(jars: List<File>, destDir: String) {

        val t1 = System.currentTimeMillis()
        logI("  ajcCompileJars begins")

        val jarsInpath = jars.joinToString(separator = File.pathSeparator)

        val ajcJrtClasspath = mProject.configurations.getByName("ajc").asPath

        val bootclasspath: String = (mProject.getAppExtension?.bootClasspath ?: return).toPath()

        val args = arrayOf<String>(
            "-1.8",
            "-showWeaveInfo",
            "-d", destDir,
            "-inpath", jarsInpath,
            "-aspectpath", destDir,
            "-classpath", ajcJrtClasspath,
            "-bootclasspath", bootclasspath,
        )

        logV("  ajcCompileJars args :${args.contentToString()}")

        Main().run(args, messageHandler)

        logI("  ajcCompileJars ends in ${(System.currentTimeMillis() - t1).toFormat(false)}")
    }


    private fun getLogMessageHandler(): MessageHandler {

        val handler = MessageHandler(false)

        for (message: IMessage in handler.errors) {

            when (message.kind) {
                IMessage.ABORT, IMessage.ERROR, IMessage.FAIL -> {
                    message.thrown?.printStackTrace()
                    logE(message.message)
                    mLogger.error(message.message)
                }
                IMessage.WARNING, IMessage.INFO, IMessage.DEBUG -> {
                    logD(message.message)
                    mLogger.debug(message.message)
                }
                else -> {
                    logI(message.message)
                    mLogger.info(message.message)
                }
            }
        }

        return handler

    }
}
