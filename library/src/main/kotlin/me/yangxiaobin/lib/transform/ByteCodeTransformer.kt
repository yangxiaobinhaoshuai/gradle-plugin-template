package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.asm.api.applyAsm
import java.io.File

interface ByteCodeTransformer {

    fun transformFile(inputFile: File)
}


class AbsByteCodeTransformer(
    private val sourceRootOutputDir: File,
) : ByteCodeTransformer {

    override fun transformFile(inputFile: File) {
        val transformedByteArray: ByteArray = inputFile.inputStream().use {
            // For Testing.
            //it.readAllBytes()
            it.applyAsm()
        }
        File(sourceRootOutputDir.path).mkdirs()
        File(sourceRootOutputDir.path + File.separator + inputFile.name)
            .writeBytes(transformedByteArray)
    }

}
