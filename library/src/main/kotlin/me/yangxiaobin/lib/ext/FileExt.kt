package me.yangxiaobin.lib.ext

import me.yangxiaobin.lib.constant.DOT_CLASS
import me.yangxiaobin.lib.constant.EXT_CLASS
import me.yangxiaobin.lib.constant.EXT_JAR
import java.io.File
import java.util.zip.ZipEntry


/* Checks if a file is a .class file. */
fun File.isClassFile() = this.isFile && this.extension == EXT_CLASS

/* Checks if a Zip entry is a .class file. */
fun ZipEntry.isClassFile() = !this.isDirectory && this.name.endsWith(DOT_CLASS)

/* CHecks if a file is a .jar file. */
fun File.isJarFile() = this.isFile && this.extension == EXT_JAR
