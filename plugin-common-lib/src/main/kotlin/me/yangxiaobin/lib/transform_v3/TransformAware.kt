package me.yangxiaobin.lib.transform_v3

import java.util.concurrent.CopyOnWriteArrayList

interface TransformAware {

    fun preTransform()

    fun postTransform()

}


object TransformAwareManager {

    @Suppress("MemberVisibilityCanBePrivate")
    val transforms: MutableList<TransformAware> = CopyOnWriteArrayList<TransformAware>()

    fun registerTransformAware(transform: TransformAware) {
        transforms += transform
    }

}
