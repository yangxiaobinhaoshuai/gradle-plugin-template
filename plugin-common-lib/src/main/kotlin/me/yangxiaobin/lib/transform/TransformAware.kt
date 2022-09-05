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

    fun transform(input: File, out: File): File
}

class ClassFileTransformer
class JarFileTransformer
class FileCopyTransformer : FileTransformer {

    override fun transform(input: File, out: File): File = input.copyTo(out).also { println("---> copy: $input from output: $out") }


}
