package me.yangxiaobin.lib.transform

import java.io.File

typealias TransformMaterials = Collection<TransformEntry>

typealias  FileTransformAction = (input: File, out: File) -> File

/**
 * 记录文件状态，记变换后的存储位置 (File)
 */
sealed interface TransformEntry {
    val input: File
    val output: File
}

data class JarTransformEntry(override val input: File, override val output: File) : TransformEntry
data class DirTransformEntry(override val input: File, override val output: File) : TransformEntry

data class DeleteTransformEntry(override val input: File, override val output: File) : TransformEntry

/**
 * 用于抽象 AGP transform task 修改字节码逻辑
 */
interface TransformAware {

    fun preTransform()

    fun doTransform(materials: TransformMaterials)

    fun postTransform()
}


interface TransformEngine {

    fun submitTransformEntry(entry: TransformEntry)

}

interface FileTransformer {

    fun transform(input: File, out: File): File
}
