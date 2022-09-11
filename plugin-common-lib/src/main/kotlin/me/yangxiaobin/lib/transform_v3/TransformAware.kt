package me.yangxiaobin.lib.transform_v3

import java.util.concurrent.CopyOnWriteArrayList


typealias ClassTransformation = (TransformTicket, ByteArray) -> ByteArray

interface TransformAware {

    fun preTransform()

    fun postTransform()

    fun getClassTransformer(): ClassTransformation

}


object TransformAwareManager {

    @Suppress("MemberVisibilityCanBePrivate")
    val transforms: MutableList<TransformAware> = CopyOnWriteArrayList<TransformAware>()

    fun registerTransformAware(transform: TransformAware) {
        transforms += transform
    }

    fun getClassTransformer(): ClassTransformation = { t: TransformTicket, bs: ByteArray ->
        transforms
            .map(TransformAware::getClassTransformer)
            .fold(bs) { acc: ByteArray, f: (TransformTicket, ByteArray) -> ByteArray -> f.invoke(t, acc) }
    }

}
