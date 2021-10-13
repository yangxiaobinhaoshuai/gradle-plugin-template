package me.yangxaobin.buildsrc.groovy

import kotlin.reflect.jvm.internal.ReflectProperties.Val
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.compile.AbstractCompile

class GroovyAspectJPlugin implements org.gradle.api.Plugin<org.gradle.api.Project> {

    private static final TAG = "GAP"

    private Logger mLogger

    @Override
    void apply(Project target) {

        mLogger = target.getLogger()

        logI("$target.name Applied GroovyAspectJPlugin")

        target.gradle.taskGraph.whenReady { TaskExecutionGraph graph ->

            List<AbstractCompile> compiles = graph.allTasks.findAll { it instanceof AbstractCompile }


            compiles.forEach { AbstractCompile compile ->

//                compile.doLast { t ->
//
//                    t.ant.taskdef(resource: "org/aspectj/tools/ant/taskdefs/aspectjTaskdefs.properties", classpath: configurations.ajc.asPath)
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
//
//                }

            }


        }
    }

    private void logI(String message){
        println "$TAG/$message"
    }
    private void logD(String message) {
        mLogger.debug("$TAG/$message")
    }

    private void logE(String message) {
        mLogger.error("$TAG/$message")
    }

}
