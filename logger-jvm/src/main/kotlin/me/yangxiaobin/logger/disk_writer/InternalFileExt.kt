package me.yangxiaobin.logger.disk_writer

import java.io.Closeable
import java.io.File


/**
 * @see kotlin.io utils.kt
 * @see File.relativeTo
 */

internal const val CARRIAGE_RETURN = "\r\n"

internal fun File.touch(): File {
    if (!this.exists()) {
        this.parentFile?.mkdirs()
        this.createNewFile()
    }
    return this
}

internal fun Closeable.closeSafely(){
    try {
        this.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
