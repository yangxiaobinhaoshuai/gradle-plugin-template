package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.log.LogDelegate
import java.io.File

private const val LOG_TAG = "Transformer"
private val defaultLogDelegate = LogDelegate(InternalLogger, LOG_TAG)

class ClassFileTransformer(private val logDelegate: LogAware = defaultLogDelegate) : FileTransformer,
    LogAware by logDelegate {

    override fun transform(input: File, out: File): File {
        TODO("Not yet implemented")
    }

}

class JarFileTransformer(private val logDelegate: LogAware = defaultLogDelegate) : FileTransformer,
    LogAware by logDelegate {

    override fun transform(input: File, out: File): File {
        TODO("Not yet implemented")
    }

}

class FileCopyTransformer(private val logDelegate: LogAware = defaultLogDelegate) : FileTransformer,
    LogAware by logDelegate {

    override fun transform(input: File, out: File): File = input.copyTo(out).also { logI("copy from: $input into: $out.") }

}
