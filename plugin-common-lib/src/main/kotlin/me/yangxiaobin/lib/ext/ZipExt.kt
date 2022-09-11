package me.yangxiaobin.lib.ext

import me.yangxiaobin.lib.asm.constant.DOT_CLASS
import org.gradle.internal.impldep.org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator
import org.gradle.internal.impldep.org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.gradle.internal.impldep.org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.gradle.internal.impldep.org.apache.commons.compress.parallel.InputStreamSupplier
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


/* Checks if a Zip entry is a .class file. */
fun ZipEntry.isClassFile() = !this.isDirectory && this.name.endsWith(DOT_CLASS)


/**
 * Depend on gradle Api
 *
 * Doc (Archivers and Compressors) : https://commons.apache.org/proper/commons-compress/examples.html
 */
fun ZipFile.parallelTransformTo(
    executor: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),
    output: File,
    transform: (ByteArray) -> ByteArray,
) {

    val creator = ParallelScatterZipCreator(executor)

    for (entry: ZipEntry in this.entries()) {

        val stream = InputStreamSupplier {

            this.getInputStream(entry).selfOr(entry.isClassFile()) { transform.invoke(it.readBytes()).inputStream() }
        }

        creator.addArchiveEntry(ZipArchiveEntry(entry), stream)
    }

    ZipArchiveOutputStream(output.outputStream().buffered()).use(creator::writeTo)
}


fun ZipFile.blockingTransformTo(
    output: File,
    transform: (ByteArray) -> ByteArray,
) {

    output
        .outputStream()
        .buffered()
        .let(::ZipOutputStream)
        .use { zos: ZipOutputStream ->

            for (entry: ZipEntry in this.entries()) {

                zos.putNextEntry(ZipEntry(entry.name))

                this.getInputStream(entry).readBytes()
                    .selfOr(entry.isClassFile(), transform::invoke)
                    .let { zos.write(it) }

                zos.closeEntry()
            }

        }
}


