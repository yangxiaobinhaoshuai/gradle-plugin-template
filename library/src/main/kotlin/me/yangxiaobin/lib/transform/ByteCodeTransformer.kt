package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.asm.api.applyAsm
import java.io.File

interface ByteCodeTransformer {

    fun transformFile(inputFile: File)
}


class AbsByteCodeTransformer(
    private val allInputs: List<File>,
    private val sourceRootOutputDir: File,
) : ByteCodeTransformer {

    override fun transformFile(inputFile: File) {
        println("---> transformFile input :${inputFile.absolutePath}")
        val transformedByteArray: ByteArray = inputFile.inputStream().use {
            it.applyAsm()
        }
        File(sourceRootOutputDir.path)
            .also { println("----> output file name :${it.name}") }
            .writeBytes(transformedByteArray)
    }

}
