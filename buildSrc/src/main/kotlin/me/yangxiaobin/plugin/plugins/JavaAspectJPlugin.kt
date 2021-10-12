package me.yangxiaobin.plugin.plugins

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.log
import me.yangxiaobin.plugin.log.BuildSrcLogger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.compile.JavaCompile
import java.io.File
import java.util.function.Consumer

class JavaAspectJPlugin : Plugin<Project> {

    private val TAG = "JAP"
    private val logI = BuildSrcLogger.log(LogLevel.INFO,TAG)

    override fun apply(p: Project) {

        logI("Applied JAP.")


        val mLogger = p.logger
//        p.gradle.taskGraph.whenReady { graph: TaskExecutionGraph ->
//
//            val javaCompile = graph.allTasks.find {
//                it is JavaCompile
//            }
//
//            if (javaCompile !is JavaCompile) return@whenReady
//
//
//            javaCompile.doLast {
//                val args = arrayOf(
//                    "-showWeaveInfo",
//                    "-1.5",
//                    "-inpath", javaCompile.destinationDir.toString(),
//                    "-aspectpath", javaCompile.classpath.asPath,
//                    "-d", javaCompile.destinationDir.toString(),
//                    "-classpath", javaCompile.classpath.asPath,
////                    "-bootclasspath", join(mAppExtension.getBootClasspath())
//                )
//                // log the ajc options
//                // log the ajc options
//                for (i in args.indices) {
//                    mLogger.debug(TAG + ":" + i + " : " + args[i])
//                }
//
//                val msgHandler: org.aspectj.bridge.MessageHandler = org.aspectj.bridge.MessageHandler()
//                // use ajc
//                // use ajc
//                org.aspectj.tools.ajc.Main().run(args, msgHandler)
//                for (message in msgHandler.getMessages(null, true)) {
//                    mLogger.error(message.message, message.thrown)
//                }
//            }
//
//            println("----> find task :$javaCompile")
//        }
    }

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
}
