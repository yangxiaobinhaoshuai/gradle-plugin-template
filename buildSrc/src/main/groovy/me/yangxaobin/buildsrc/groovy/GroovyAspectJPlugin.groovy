package me.yangxaobin.buildsrc.groovy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.compile.AbstractCompile

class GroovyAspectJPlugin implements Plugin<Project> {

    private static final TAG = "GAP"

    private Logger mLogger

    @Override
    void apply(Project target) {

        mLogger = target.getLogger()

        logI("$target.name Applied GroovyAspectJPlugin")

        resolveAspectJJrtClasspath(target)

        setupAspectJCompilerArgs(target)

    }

    private void resolveAspectJJrtClasspath(Project project) {

        project.configurations {
            ajc
        }

        project.dependencies.add("ajc","org.aspectj:aspectjtools:1.9.7")
    }

    private void setupAspectJCompilerArgs(Project project) {

        project.gradle.taskGraph.whenReady { TaskExecutionGraph graph ->

            List<AbstractCompile> compiles = graph.allTasks.findAll { it instanceof AbstractCompile } as List<AbstractCompile>

            String aspectJJrtClasspath = project.configurations.ajc.asPath
            // TODO
            String destDir = "$project.buildDir/classes/java/main"

            logI("""
compiles are :${compiles.collect { it.name }}
jrtClasspath :$aspectJJrtClasspath
destDir :$destDir
""")

            compiles.forEach { AbstractCompile compile ->

                compile.doLast { Task t ->

                    logI("${t.name} do last begins.")

                    t.ant.taskdef(resource: "org/aspectj/tools/ant/taskdefs/aspectjTaskdefs.properties", classpath: aspectJJrtClasspath)
//
//                    t.ant.iajc(
//                            source: "1.8",
//                            target: "1.8",
//                            destDir: "$buildDir/classes/java/main",
//                            maxmem: "512m",
//                            fork: "true",
//                    ) {
//                        sourceroots {
//                            sourceSets.main.java.srcDirs.each {
//                                println " file :$it.absolutePath"
//                                pathelement(location: it.absolutePath)
//                            }
//                        }
//                    }

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
