package me.yangxiaobin.plugin.plugins

import me.yangxiaobin.lib.BasePlugin
import me.yangxiaobin.lib.ext.SourceLanguage
import me.yangxiaobin.lib.ext.getSourceSetDirs
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.compile.AbstractCompile
import java.io.File

@Deprecated(message = "replaced with java-aspectj-plugin/JavaAspectJPlugin")
class JavaAspectJPlugin : BasePlugin() {

    override val TAG = "JAP"

    override fun apply(p: Project) {
        super.apply(p)

        logI("Applied JAP.")

        resolveAspectJJrtClasspath(p)

        configJarTask(p)

        configCompileTask(p)
    }


    private fun resolveAspectJJrtClasspath(project: Project) {

        project.configurations.create("ajc")
        project.configurations.create("ajcJrt")

        project.dependencies.add("ajc", "org.aspectj:aspectjtools:1.9.7")
        project.dependencies.add("ajcJrt", "org.aspectj:aspectjrt:1.9.7")
    }

    private fun configJarTask(project: Project) {
        project.gradle.taskGraph.whenReady { graph ->
            (graph.allTasks.find { it.name.contains("jar") } as? AbstractCopyTask)?.duplicatesStrategy =
                DuplicatesStrategy.EXCLUDE
        }
    }


    private fun configCompileTask(project: Project) {

        project.gradle.taskGraph.whenReady { graph: TaskExecutionGraph ->

            val compiles = graph.allTasks.filterIsInstance<AbstractCompile>()

            compiles.forEach { compile -> compile.doLast(::doLastCompileAction) }

        }
    }

    private fun doLastCompileAction(t: Task) {

        if (t !is AbstractCompile) return

        val curCompileName = t.name

//        logI("current compile :$curCompileName")

        val (destDir, sourceroots, jrtPath, toolPath) = calculateAjcParams(t)

        val ajcArgs = arrayOf(
            "-1.8",
            "-d", destDir,
            "-inpath", jrtPath,
            "-sourceroots", sourceroots,
        )

        val execCmdPrefix = listOf("java", "-cp", toolPath, AJC_TOOLS_MAIN)

        val execCmds = execCmdPrefix + ajcArgs

        mProject.exec { it.commandLine(execCmds) }
    }

    private fun calculateAjcParams(compile: AbstractCompile): AjcParam {

        val language = compile.name.substring("compile".length).toUpperCase()

        val destDir: String = compile.destinationDir.toString()

        val sourceroots: String =
            mProject.getSourceSetDirs(SourceLanguage.valueOf(language)).joinToString(separator = File.pathSeparator)

        val ajcJrtClasspath: String = mProject.configurations.getByName("ajcJrt").asPath

        val ajcToolsClasspath: String = mProject.configurations.getByName("ajc").asPath


  /*      logI(
            """
            ajc params:
            destDir     :$destDir
            sourceroots :$sourceroots
            jrt         :$ajcJrtClasspath
            tools       :$ajcToolsClasspath
        """.trimIndent()
        )*/

        return AjcParam(destDir, sourceroots, ajcJrtClasspath, ajcToolsClasspath)
    }

    data class AjcParam(
        val destDir: String,
        val sourceroots: String,
        val ajcJrtPath: String,
        val ajcToolPath: String,
    )

    private companion object {
        private const val AJC_TOOLS_MAIN = "org.aspectj.tools.ajc.Main"
    }
}
