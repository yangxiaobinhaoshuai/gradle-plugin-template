package me.yangxiaobin.aspectj

import me.yangxiaobin.lib.base.BasePlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.compile.AbstractCompile

class GroovyAspectJPlugin extends BasePlugin {

    private static final TAG = "GAP"

    @Override
    void apply(Project target) {
        super.apply(target)

        logI("$target.name Applied GroovyAspectJPlugin")

        resolveAspectJJrtClasspath(target)

        configJarTask(target)

        setupAspectJCompilerArgs(target)
    }

    private void resolveAspectJJrtClasspath(Project project) {

        project.configurations { ajc }

        project.dependencies.add("ajc", "org.aspectj:aspectjtools:1.9.7")
    }


    private void configJarTask(Project project) {

        project.gradle.taskGraph.whenReady { TaskExecutionGraph graph ->

            graph.allTasks.find { it.name.contains("jar") }?.configure {
                duplicatesStrategy DuplicatesStrategy.EXCLUDE
            }
        }
    }

    private void setupAspectJCompilerArgs(Project project) {

        project.gradle.taskGraph.whenReady { TaskExecutionGraph graph ->

            List<AbstractCompile> compiles = graph.allTasks.findAll { it instanceof AbstractCompile } as List<AbstractCompile>

            String aspectJJrtClasspath = project.configurations.ajc.asPath

            compiles.forEach { AbstractCompile compile ->

                String sourceVariantName = compile.name
                        .substring("compile".length())
                        .toLowerCase()

                String destDir = "$project.buildDir/classes/$sourceVariantName/main"

                logI("""
current compiles is :$sourceVariantName
jrtClasspath :$aspectJJrtClasspath
destDir :$destDir
""")

                compile.doLast { Task t ->

                    logI("${t.name} do last begins.")

                    t.ant.taskdef(resource: "org/aspectj/tools/ant/taskdefs/aspectjTaskdefs.properties", classpath: aspectJJrtClasspath)

                    t.ant.iajc(
                            source: "1.8",
                            target: "1.8",
                            destDir: destDir,
                            maxmem: "512m",
                            fork: "true",
                    ) {
                        sourceroots {

                            def curSourceDirs = project.sourceSets.main[sourceVariantName].srcDirs
                                    .findAll { File sourceDir -> sourceDir.exists() }

                            curSourceDirs.each {
                                pathelement(location: it.absolutePath)
                            }

                        }
                    }

                }

            }

        }
    }

    private static void logI(String message) {
        println "$TAG/$message"
    }

    private void logD(String message) {
        mLogger.debug("$TAG/$message")
    }

    private void logE(String message) {
        mLogger.error("$TAG/$message")
    }

}
