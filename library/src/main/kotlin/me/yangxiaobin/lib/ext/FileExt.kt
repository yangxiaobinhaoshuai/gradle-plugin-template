package me.yangxiaobin.lib.ext

import me.yangxiaobin.lib.asm.constant.DOT_CLASS
import me.yangxiaobin.lib.asm.constant.EXT_CLASS
import me.yangxiaobin.lib.asm.constant.EXT_JAR
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.parallel.InputStreamSupplier
import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


/* Checks if a file is a .class file. */
fun File.isClassFile() = this.isFile && this.extension == EXT_CLASS

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


/**
 * convert bootClassPath<File>  to String separated by ":"
</File> */
fun Collection<File>?.toPath(): String {
    require(!(this == null || this.isEmpty())) { "The parameters can't be null." }
    val sb = StringBuilder()
    this.forEach { s: File ->
        sb.append(s.absolutePath).append(File.pathSeparator)
    }
    val lastIndexOf = sb.lastIndexOf(File.pathSeparator)
    return sb.toString().substring(0, lastIndexOf)
}

fun File.touch() = apply {
    if (!this.exists()) {
        this.parentFile?.mkdirs()
        this.createNewFile()
    }
}


fun ZipFile.parallelTransformTo(output: File, transform: (ByteArray) -> ByteArray) {
    val creator = ParallelScatterZipCreator()

    this.entries().asSequence().forEach { entry: ZipEntry ->

        val stream = InputStreamSupplier {
            this.getInputStream(ZipEntry(entry.name))
                .transformIf(entry.isClassFile()) { ins: InputStream ->
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
                    .transformIf(entry.isClassFile()) { ins ->
                        transform.invoke(ins.readBytes()).inputStream()
                    }
                    .use { it.copyTo(zos) }
            }

            zos.closeEntry()
        }
}



