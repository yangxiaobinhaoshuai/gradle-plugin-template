package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.TransformAction
import java.io.File
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1

typealias TransformMaterials = Collection<TransformEntry>

sealed interface TransformEntry {
    val input: File
    val output: File
}

data class JarTransformEntry(override val input: File, override val output: File) : TransformEntry
data class DirTransformEntry(override val input: File, override val output: File) : TransformEntry
data class DeleteTransformEntry(override val input: File, override val output: File) : TransformEntry




interface TransformAware {

    fun preTransform()

    fun doTransform(materials: TransformMaterials)

    fun postTransform()
}


interface TransformEngine {

    fun submitTransformAction(transformActions: List<TransformAction>)
}




interface TypeTransformer<T> {

    fun syncTransform(input: T, output: T)
}

interface FileTypeTransformer : TypeTransformer<File> {

    override fun syncTransform(input: File, output: File)
}

interface ClassByteTypeTransformer : TypeTransformer<ByteArray> {

    override fun syncTransform(input: ByteArray, output: ByteArray)

    fun transform(input: ByteArray): ByteArray
}

fun interface ByteArrayConverter {
    fun transform(input: ByteArray): ByteArray
}

open class ByteArrayConverterDelegate(delegate: ByteArrayConverter) : ByteArrayConverter by delegate
