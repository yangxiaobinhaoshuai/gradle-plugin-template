package me.yangxiaobin.lib.ext

import me.yangxiaobin.lib.asm.constant.EXT_CLASS
import me.yangxiaobin.lib.asm.constant.EXT_JAR
import java.io.File

/**
 * @see kotlin.io.copyTo
 */

/* Checks if a file is a .class file. */
fun File.isClassFile() = this.isFile && !this.isDirectory && this.extension == EXT_CLASS

/* Checks if a file is a .jar file. */
fun File.isJarFile() = this.isFile && this.extension == EXT_JAR

fun File.rename(newName: String): Boolean {
    if (this.name == newName) return false

    val newFile = File(this.parentFile, newName).touch()
    return this.renameTo(newFile)
}

fun File.renamed(newName: String): File {
    if (this.name == newName) throw IllegalArgumentException("newName can't be same with original file:$this")

    val newFile = File(this.parentFile, newName).touch()
    this.renameTo(newFile)
    return newFile
}


//private val rwLock = ReadWriteLock()
/**
 * Not thread safe
 */
fun File.touch(): File = apply {
    if (!this.exists()) {
        this.parentFile?.mkdirs()
        this.createNewFile()
    }
}

/**
 * mkdirs 在多线程下会返回 false 表示当前目录已经创建, 这里 Catch 住 [copyTo] 抛的异常
 * https://stackoverflow.com/a/29488734/10247834
 */
fun File.safeCopyTo(output: File) {
    if (this.isDirectory) {
        try {
            this.copyTo(output)
        } catch (e: FileSystemException) {
            //e.printStackTrace()
        }
    } else this.copyTo(output)
}


