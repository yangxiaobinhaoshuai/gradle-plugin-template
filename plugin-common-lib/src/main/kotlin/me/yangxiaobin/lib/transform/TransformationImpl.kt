package me.yangxiaobin.lib.transform

import java.io.File

typealias LogAction = (String) -> Unit


class ClassFileTransformer(override val logger: LogAction = {}) : FileTransformer {

    override fun transform(input: File, out: File): File {
        TODO("Not yet implemented")
    }

}

class JarFileTransformer(override val logger: LogAction = {}) : FileTransformer {

    override fun transform(input: File, out: File): File {
        TODO("Not yet implemented")
    }

}

class FileCopyTransformer(override val logger: LogAction = {}) : FileTransformer {

    override fun transform(input: File, out: File): File =
        input.copyTo(out).also { logger("copied: $input into output: $out") }

}
