package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.log.LogAware
import java.io.File

class ClassFileTransformer(private val logDelegate: LogAware) : FileTransformer, LogAware by logDelegate {

    override fun transform(input: File, out: File): File {
        TODO("Not yet implemented")
    }

}

class JarFileTransformer(private val logDelegate: LogAware) : FileTransformer, LogAware by logDelegate {

    override fun transform(input: File, out: File): File {
        TODO("Not yet implemented")
    }

}

class FileCopyTransformer(private val logDelegate: LogAware) : FileTransformer, LogAware by logDelegate {

    override fun transform(input: File, out: File): File =
        input.copyTo(out).also { logI("copied: $input into output: $out.") }

}
