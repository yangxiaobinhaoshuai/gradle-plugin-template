package com.boilerplate

import me.yangxiaobin.lib.BasePlugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File

class Bit64CheckerPlugin : BasePlugin() {

    // File absolute path.
    private val v7Set = mutableSetOf<String>()
    private val v8aSet = mutableSetOf<String>()


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

                    val intersection = v7Set intersect v8aSet
                    val targets = v7Set - intersection

                    println(" 32 so文件: ${v7Set.size}, ${v7Set.joinToString(separator = "\r\n")}.")
                    println("----------------------------------------- \r\n")
                    println(" 64 so文件: ${v8aSet.size}, ${v8aSet.joinToString(separator = "\r\n")}.")
                    println("----------------------------------------- \r\n")
                    println(" 未适配 64 位so文件: ${targets.size}, ${targets.joinToString(separator = "\r\n")}.")

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

                // /Users/yangxiaobin/.gradle/caches/transforms-2/files-2.1/497341f4f6b8a19f92c24ab984c1f794/jetified-tunnel-release/jni/armeabi-v7a/libwg-go.so

                // 去掉 armeabi-v7a 或者 arm64-v8a
                if (file.absolutePath.contains("armeabi-v7a")) {
                    val truncatedPath = file.absolutePath.replace("/armeabi-v7a", "")
                    v7Set += truncatedPath
                }

                if (file.absolutePath.contains("arm64-v8a")) {
                    val truncatedPath = file.absolutePath.replace("/arm64-v8a", "")
                    v8aSet += truncatedPath
                }

            }
        }
    }
}
