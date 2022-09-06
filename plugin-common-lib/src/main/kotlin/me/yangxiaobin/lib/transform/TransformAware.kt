package me.yangxiaobin.lib.transform

import java.io.File

typealias TransformInput = Collection<TransformEntry>

/**
 * 记录文件状态，记变换后的存储位置 (File)
 */
data class TransformEntry(val input: File, val output: File, val transformAction: FileTransformAction)

/**
 * 用于抽象 AGP transform task 修改字节码逻辑
 */
interface TransformAware {

    fun preTransform()

    fun doTransform(input: TransformInput)

    fun postTransform()
}


typealias  FileTransformAction = (input: File, out: File) -> File

interface ParallelTransformEngine {

    /**
     * 添加文件转换任务
     */
    fun submitTransformAction(action: FileTransformAction)

}

interface FileTransformer {

    val logger: LogAction get() = {}

    fun transform(input: File, out: File): File
}
