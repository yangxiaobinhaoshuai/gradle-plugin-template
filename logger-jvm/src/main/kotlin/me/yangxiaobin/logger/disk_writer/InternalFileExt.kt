package me.yangxiaobin.logger.disk_writer

import java.io.File


/**
 * @see kotlin.io utils.kt
 * @see File.relativeTo
 */


internal fun File.touch(): File {
    if (!this.exists()) {
        this.parentFile?.mkdirs()
        this.createNewFile()
    }
    return this
}
