package me.yangxiaobin.lib.ext

import me.yangxiaobin.lib.asm.constant.DOT_CLASS
import me.yangxiaobin.lib.asm.constant.EXT_CLASS
import me.yangxiaobin.lib.asm.constant.EXT_JAR
import org.gradle.internal.impldep.org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator
import org.gradle.internal.impldep.org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.gradle.internal.impldep.org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.gradle.internal.impldep.org.apache.commons.compress.parallel.InputStreamSupplier
import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * @see kotlin.io.copyTo
 */

/* Checks if a file is a .class file. */
fun File.isClassFile() = this.isFile && !this.isDirectory && this.extension == EXT_CLASS

/* Checks if a Zip entry is a .class file. */
fun ZipEntry.isClassFile() = !this.isDirectory && this.name.endsWith(DOT_CLASS)

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


fun ZipFile.parallelTransformTo(output: File, transform: (ByteArray) -> ByteArray) {
    val creator = ParallelScatterZipCreator()

    this.entries().asSequence().forEach { entry: ZipEntry ->

        val stream = InputStreamSupplier {
            this.getInputStream(ZipEntry(entry.name))
                .toIf(entry.isClassFile()) { ins: InputStream ->
                    transform.invoke(ins.readBytes()).inputStream()
                }
        }

        creator.addArchiveEntry(ZipArchiveEntry(entry), stream)
    }

    ZipArchiveOutputStream(output.outputStream().buffered()).use(creator::writeTo)
}


fun ZipFile.simpleTransformTo(output: File, transform: (ByteArray) -> ByteArray) {

    output.outputStream().buffered().let { ZipOutputStream(it) }
        .use { zos ->
            this.entries().asSequence().forEach { entry: ZipEntry ->
                zos.putNextEntry(ZipEntry(entry.name))
                this.getInputStream(entry)
                    .toIf(entry.isClassFile()) { ins ->
                        transform.invoke(ins.readBytes()).inputStream()
                    }
                    .use { it.copyTo(zos) }
            }

            zos.closeEntry()
        }
}



