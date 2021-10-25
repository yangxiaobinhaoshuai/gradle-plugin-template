package me.yangxiaobin.aspectandroid

import com.android.build.gradle.internal.pipeline.TransformTask
import me.yangxiaobin.lib.base.BasePlugin
import me.yangxiaobin.lib.ext.*
import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.LogLevel
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

class AspectAndroidPlugin : BasePlugin() {

    override val TAG: String get() = "AAP"

    private val messageHandler by lazy { MessageHandler(false) }

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
        if (!jars.isNullOrEmpty()) ajcCompileJars(jars)
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

            populateMessageHandler()
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

        // TODO hardcode here.
        val runtimeClasspath = mProject.configurations.find { it.name == "debugRuntimeClasspath" }?.asPath ?: return

        // 1. rename
        val prefix = "pre-ajc-"

        jars.map { it.renamed("$prefix${it.name}") }
            .forEach { preJar ->

                val originalName = preJar.parent + File.separator + preJar.name.substring(prefix.length)

                val jarInpath = preJar.absolutePath

                val destDir = preJar.parent

                val compilePath =
                    (mProject.tasks.find { it is KotlinCompile } as? KotlinCompile)?.classpath?.asPath ?: ""

                // TODO more precise classpath.
                val cp: String = (runtimeClasspath.split(File.pathSeparator) + compilePath.split(File.pathSeparator))
                    .distinct()
                    .filterNot { it.split(File.separator).last() == "jetified-kotlin-reflect-1.5.31.jar" }
                    .filterNot { it.endsWith(".aar") }
                    .joinToString(separator = File.pathSeparator)

                val ajcJrtClasspath = mProject.configurations.getByName("ajc").asPath

                val bootclasspath: String = (mProject.getAppExtension?.bootClasspath ?: return).toPath()

                val args = arrayOf<String>(
                    "-1.8",
                    "-showWeaveInfo",
                    "-d", destDir,
                    "-inpath", jarInpath,
                    "-aspectpath", destDir,
                    "-classpath", "$ajcJrtClasspath:$cp",
                    "-outjar", originalName,
                    "-bootclasspath", bootclasspath,
                )

                logV("  ajcCompileJars args :${args.contentToString()}")

                Main().run(args, messageHandler)

                populateMessageHandler()

                preJar.delete()
            }

        logI("  ajcCompileJars ends in ${(System.currentTimeMillis() - t1).toFormat(false)}")
    }


    private fun populateMessageHandler() {

        for (message: IMessage in messageHandler.getMessages(null, true)) {

            when (message.kind) {
                IMessage.ABORT, IMessage.ERROR, IMessage.FAIL -> {
                    message.thrown?.printStackTrace()
                    //logE(message.message)
                    mLogger.error(message.message)
                }
                IMessage.WARNING, IMessage.INFO, IMessage.DEBUG -> {
                    //logD(message.message)
                    //mLogger.debug(message.message)
                }
                else -> {
                    //logI(message.message)
                    //mLogger.info(message.message)
                }
            }
        }

    }
}
