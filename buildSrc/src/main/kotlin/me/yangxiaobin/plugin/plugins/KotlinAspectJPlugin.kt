package me.yangxiaobin.plugin.plugins

import me.yangxiaobin.lib.ext.currentJreClasspath
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.log
import me.yangxiaobin.plugin.log.BuildSrcLogger
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.util.function.Consumer

class JavaAspectJPlugin : Plugin<Project> {

    private val TAG = "JAP"

    private val logI = BuildSrcLogger.log(LogLevel.INFO, TAG)

    private lateinit var mProject: Project

    private lateinit var mLogger: Logger


    //ajc args: [
    // -showWeaveInfo,
    // -1.5,
    // -inpath,
    //   /Users/yangxiaobin/DevelopSpace/IDEA/hugo/hugo-example/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes,

    // -aspectpath,
    //    /Users/yangxiaobin/.gradle/caches/transforms-1/files-1.1/hugo-runtime-1.2.1.aar/a5ace53027ccfdad59bde16452f2b386/jars/classes.jar
    //   :/Users/yangxiaobin/.gradle/caches/modules-2/files-2.1/com.jakewharton.hugo/hugo-annotations/1.2.1/b01150795c5cdca1eb7e501bf00f105ff0e31501/hugo-annotations-1.2.1.jar
    //   :/Users/yangxiaobin/.gradle/caches/modules-2/files-2.1/org.aspectj/aspectjrt/1.8.6/a7db7ea5f7bb18a1cbd9f24edd0e666504800be/aspectjrt-1.8.6.jar,
    //
    // -d,
    //    /Users/yangxiaobin/DevelopSpace/IDEA/hugo/hugo-example/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes,
    //
    // -classpath,
    //    /Users/yangxiaobin/.gradle/caches/transforms-1/files-1.1/hugo-runtime-1.2.1.aar/a5ace53027ccfdad59bde16452f2b386/jars/classes.jar
    //    :/Users/yangxiaobin/.gradle/caches/modules-2/files-2.1/com.jakewharton.hugo/hugo-annotations/1.2.1/b01150795c5cdca1eb7e501bf00f105ff0e31501/hugo-annotations-1.2.1.jar
    //    :/Users/yangxiaobin/.gradle/caches/modules-2/files-2.1/org.aspectj/aspectjrt/1.8.6/a7db7ea5f7bb18a1cbd9f24edd0e666504800be/aspectjrt-1.8.6.jar,
    //
    // -bootclasspath, /Users/yangxiaobin/Library/Android/sdk/platforms/android-29/android.jar]
    override fun apply(p: Project) {

        logI("Applied JAP.")

        mProject = p
        mLogger = p.logger

        val logger by lazy { p.logger }

        resolveAspectJJrtClasspath(p)

        val sourceSets = p.properties["sourceSets"] as SourceSetContainer
        val main: SourceSet = sourceSets.getByName("main")

        val sourceroots = p.projectDir.absolutePath + "/src/main"


        val jreClassPath = (currentJreClasspath ?: "")
            .split(File.pathSeparator)
            .filterNot { it.endsWith(SUN_RSA_SIGN_JAR) || it.endsWith(CLASSES) }
            .joinToString(separator = File.pathSeparator)


        p.gradle.taskGraph.whenReady { graph: TaskExecutionGraph ->

            val javaCompile: AbstractCompile =
                (graph.allTasks.find { it is JavaCompile } as? JavaCompile) ?: return@whenReady


            val compiles = graph.allTasks.filterIsInstance<AbstractCompile>()


            val javaInpath = javaCompile.destinationDir.toString()

            // same as destDir
            val inpath: String = compiles.joinToString { it.destinationDir.toString() }

            javaCompile.doLast {
                compileDoLastAction(javaInpath, sourceroots)
            }
        }
    }

    private fun compileDoLastAction(
        javaInpath: String,
        sourceroots: String,
    ) {
        val ajcToolPath = mProject.configurations.getByName("ajc").asPath
        val ajcJrtPath = mProject.configurations.getByName("ajcJrt").asPath

        logI("Java compile do last begins.")

        val args = arrayOf(
            "-showWeaveInfo",
            "-source", "1.8",
            "-target", "1.8",
            "-verbose",
            "-d", javaInpath,
            "-inpath", ajcJrtPath,
            "-aspectpath", ajcToolPath,
            "-sourceroots", sourceroots,
            //"-bootclasspath", jreClassPath
        )

        // log the ajc options
        for (i in args.indices) {
            logI(TAG + ":" + i + " : " + args[i])
        }

        val msgHandler: MessageHandler = MessageHandler()
        // use ajc
        Main().run(args, msgHandler)
        for (message: IMessage in msgHandler.getMessages(null, true)) {

            message.thrown?.printStackTrace()

            mLogger.error(
                """
                     message handler msg : ${message.message} 
                     message.class : ${message.javaClass}
                     message.sourceStart : ${message.sourceStart}
                     message.sourceEnd : ${message.sourceEnd}
                     thrown: ${message.thrown}
                """.trimIndent()
            )
        }
    }

    private fun resolveAspectJJrtClasspath(project: Project) {

        project.configurations.create("ajc")
        project.configurations.create("ajcJrt")

        project.dependencies.add("ajc", "org.aspectj:aspectjtools:1.9.7")
        project.dependencies.add("ajcJrt", "org.aspectj:aspectjrt:1.9.7")

    }

    // TODO
    private fun doLastAction(task: Task) {

    }


    private fun log(msg: String) = println("$TAG ====> $msg")

    /**
     * convert bootClassPath<File>  to String separated by ":"
    </File> */
    private fun join(list: List<File>?): String {
        require(!(list == null || list.isEmpty())) { "The parameters can't be null." }
        val sb = StringBuilder()
        list.forEach(
            Consumer { s: File ->
                sb.append(s.absolutePath).append(File.pathSeparator)
            }
        )
        val lastIndexOf = sb.lastIndexOf(File.pathSeparator)
        return sb.toString().substring(0, lastIndexOf)
    }

    private companion object {
        /**
         *  Jre classpath maybe different among different versions, some of them maybe absence.
         *
         *  Refer to : https://stackoverflow.com/questions/5971964/when-should-i-use-file-separator-and-when-file-pathseparator
         */
        private const val SUN_RSA_SIGN_JAR = "sunrsasign.jar"
        private const val CLASSES = "classes"
    }
}
