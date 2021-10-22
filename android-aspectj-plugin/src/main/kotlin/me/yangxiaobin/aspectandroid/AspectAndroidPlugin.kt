package me.yangxiaobin.aspectandroid

import me.yangxiaobin.lib.base.BasePlugin
import me.yangxiaobin.lib.ext.getAppExtension
import me.yangxiaobin.lib.ext.toPath
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.tasks.compile.JavaCompile
import java.io.File
import java.lang.NullPointerException

class AspectAndroidPlugin : BasePlugin() {

    override val TAG: String get() = "AAP"

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
                .also { cs-> logI("abs compiles :${cs.map { it.javaClass }}") }
                .forEach {  compile ->

                    if (compile is JavaCompile) return@whenReady

                    compile.doLast { doAjcCompilation(compile) }
                }

        }


    }


    private fun doAjcCompilation(compile: AbstractCompile) {

        logI("""
            ${compile.name} doAjcCompilation begins.
            compile input :${compile.inputs.files.asPath}
            compile output :${compile.outputs.files.asPath}
        """.trimIndent())

        val destDir = compile.destinationDir.toString()

        val ajcJrtClasspath = mProject.configurations.getByName("ajc").asPath

        val bootclasspath: String = (mProject.getAppExtension?.bootClasspath ?: return).toPath()


        val args = arrayOf<String>(
            "-1.8",
            "-showWeaveInfo",
//            "-verbose",

            "-d", destDir,
            "-aspectpath","/Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/androidapp/build/intermediates/javac/debug/classes",
            "-inpath", ajcJrtClasspath,
            "-sourceroots", destDir,
            "-bootclasspath", bootclasspath,
        )

        logI("ajc params :${args.contentToString()}")

        Main().run(args, getLogMessageHandler())
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
