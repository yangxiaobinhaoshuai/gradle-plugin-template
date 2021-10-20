package me.yangxiaobin.aspectandroid

import me.yangxiaobin.lib.base.BasePlugin
import me.yangxiaobin.lib.ext.SourceLanguage
import me.yangxiaobin.lib.ext.getAppExtension
import me.yangxiaobin.lib.ext.getSourceSetDirs
import me.yangxiaobin.lib.ext.toPath
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.gradle.api.Project
import java.io.File

class AspectAndroidPlugin : BasePlugin() {

    override val TAG: String get() = "AAP"

    override fun apply(p: Project) {
        super.apply(p)

        logI("${p.name} applied AspectAndroidPlugin.")

        configAjcClasspath()

        val aspectTransform = AspectTransform(mProject)

        p.afterEvaluate { p.getAppExtension?.registerTransform(aspectTransform) }

        setupAjcCompilation()
    }

    private fun configAjcClasspath() {
        mProject.configurations.create("ajc")

        mProject.dependencies.add("ajc", "org.aspectj:aspectjrt:1.9.7")
    }

    private fun setupAjcCompilation() {

        mProject.afterEvaluate {
            mProject.getAppExtension
                ?.applicationVariants
                ?.all {
                    println("--->  all all variant :${it.name}")
                }

        }

    }


    private fun doAjcCompilation() {

        val destDir = mProject.buildDir

        val ajcJrtClasspath = mProject.configurations.getByName("ajc").asPath

        val currentLanguage = "JAVA"
        val sourceroots = mProject.getSourceSetDirs(SourceLanguage.valueOf(currentLanguage))
            .joinToString(separator = File.pathSeparator)

        val bootclasspath: String = (mProject.getAppExtension?.bootClasspath ?: return).toPath()


        val args = arrayOf(
            "-1.8",
            "-showWeaveInfo",

            "-d", "",
            "-inpath", ajcJrtClasspath,
            "-sourceroots", sourceroots,
            "-bootclasspath", bootclasspath,
        )

        logI("ajc params :${args.contentToString()}")

        //Main().run(args, getLogMessageHandler())
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
