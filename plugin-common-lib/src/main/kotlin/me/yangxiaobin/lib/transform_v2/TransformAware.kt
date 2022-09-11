package me.yangxiaobin.lib.transform_v2

import me.yangxiaobin.lib.TransformAction
import java.io.File

@Deprecated("see v3")
typealias TransformMaterials = Collection<TransformEntry>

@Deprecated("see v3")
sealed interface TransformEntry {
    val input: File
    val output: File
}

data class JarTransformEntry(override val input: File, override val output: File) : TransformEntry
data class DirTransformEntry(override val input: File, override val output: File) : TransformEntry
data class DeleteTransformEntry(override val input: File, override val output: File) : TransformEntry




@Deprecated("see v3")
interface TransformAware {

    fun preTransform()

    fun doTransform(materials: TransformMaterials)

    fun postTransform()
}


@Deprecated("see v3")
interface TransformEngine {

    fun submitTransformAction(transformActions: List<TransformAction>)
}



@Deprecated("see v3")
interface TypeTransformer<T> {

    fun syncTransform(input: T, output: T)
}

@Deprecated("see v3")
interface FileTypeTransformer : TypeTransformer<File> {

    override fun syncTransform(input: File, output: File)
}

interface ClassByteTypeTransformer : TypeTransformer<ByteArray> {

    override fun syncTransform(input: ByteArray, output: ByteArray)

    fun transform(input: ByteArray): ByteArray
}

@Deprecated("see v3")
fun interface ByteArrayConverter {
    fun transform(input: ByteArray): ByteArray
}

@Deprecated("see v3")
open class ByteArrayConverterDelegate(delegate: ByteArrayConverter) : ByteArrayConverter by delegate
