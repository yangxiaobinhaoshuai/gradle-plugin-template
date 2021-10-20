package me.yangxiaobin.aspectandroid

import me.yangxiaobin.lib.base.BasePlugin
import me.yangxiaobin.lib.ext.getAppExtension
import me.yangxiaobin.lib.ext.toPath
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.tasks.compile.JavaCompile
import java.io.File

class AspectAndroidPlugin : BasePlugin() {

    override val TAG: String get() = "AAP"
    private val sourcerootSet = mutableSetOf<String>()

    override fun apply(p: Project) {
        super.apply(p)

        logI("${p.name} applied AspectAndroidPlugin.")

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

            graph.allTasks.filterIsInstance<AbstractCompile>()
                .also { logI("abs compiles :$it") }
                .forEach {  compile ->

                    if (compile is JavaCompile) return@whenReady

                    compile.doLast { doAjcCompilation(compile) }
                }

        }


    }


    private fun doAjcCompilation(compile: AbstractCompile) {

        logI(" ${compile.name} doAjcCompilation begins.")

        val destDir = compile.destinationDir.toString()

        sourcerootSet.add(destDir)

        val actualsourceroots = sourcerootSet.joinToString(separator = File.pathSeparator)

        val ajcJrtClasspath = mProject.configurations.getByName("ajc").asPath

        val bootclasspath: String = (mProject.getAppExtension?.bootClasspath ?: return).toPath()


        val args = arrayOf(
            "-1.8",
            "-showWeaveInfo",

            "-d", destDir,
            "-inpath", ajcJrtClasspath,
            "-sourceroots", actualsourceroots,
            "-bootclasspath", bootclasspath,
        )

        logI("ajc params :${args.contentToString()}")

        Main().run(args, getLogMessageHandler())
    }

    private fun getLogMessageHandler(): MessageHandler {

        val handler = MessageHandler(true)

        for (message: IMessage in handler.unmodifiableListView) {

            when (message.kind) {
                IMessage.ABORT, IMessage.ERROR, IMessage.FAIL -> {
                    message.thrown?.printStackTrace()
                    logE(message.message)
                }
                IMessage.WARNING, IMessage.INFO, IMessage.DEBUG -> {
                    logD(message.message)
                }
                else -> {
                    logI(message.message)
                }
            }
        }

        return handler

    }
}
