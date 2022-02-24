package com.boilerplate

import me.yangxiaobin.lib.BasePlugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File

class Bit64CheckerPlugin : BasePlugin() {

    override fun apply(p: Project) {
        super.apply(p)

        p.tasks.whenTaskAdded { t: Task ->

            // e.g. mergeAppstoreDebugNativeLibs
            if (t.name.contains("merge", ignoreCase = true) && t.name.contains("NativeLibs")) {
                t.doFirst {

                    println("---------- ${t.name} 开始查找so文件归属 ---------")

                    t.inputs.files.forEach { file ->
                        printDir(File(file.absolutePath))
                    }

                    println("---------- ${t.name} 查找so文件归属结束 ---------")
                }
            }
        }

    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun printDir(file: File?) {

        if (file != null) {
            if (file.isDirectory) {
                file.listFiles().forEach { printDir(it) }
            } else if (file.absolutePath.endsWith(".so")) {
                println("so文件路径: $file.absolutePath")
            }
        }
    }
}
