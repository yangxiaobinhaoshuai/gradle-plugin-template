package me.yangxiaobin.plugin.plugins

import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.compile.AbstractCompile

class GroovyAspectJPlugin implements org.gradle.api.Plugin<org.gradle.api.Project> {


    @Override
    void apply(Project target) {


        target.gradle.taskGraph.whenReady { TaskExecutionGraph graph ->

            List<AbstractCompile> compiles = graph.allTasks.findAll { it instanceof AbstractCompile }


            compiles.forEach { AbstractCompile compile ->

                compile.doLast { t->

                    t.ant.taskdef(resource: "org/aspectj/tools/ant/taskdefs/aspectjTaskdefs.properties", classpath: configurations.ajc.asPath)

                    t.ant.iajc(
                            source: "1.8",
                            target: "1.8",
                            destDir: "$buildDir/classes/java/main",
                            maxmem: "512m",
                            fork: "true",
                    ) {
                        sourceroots {
                            sourceSets.main.java.srcDirs.each {
                                println " file :$it.absolutePath"
                                pathelement(location: it.absolutePath)
                            }
                        }
                    }

                }

            }


        }
    }
}
