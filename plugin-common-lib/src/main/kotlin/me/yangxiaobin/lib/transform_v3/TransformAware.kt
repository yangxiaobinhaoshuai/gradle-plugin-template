package me.yangxiaobin.lib.transform_v3

import me.yangxiaobin.lib.ext.requireAppExtension
import org.gradle.api.Project
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

    fun registerTransformAware(
        onPreTransform: (() -> Unit)? = null,
        onPostTransform: (() -> Unit)? = null,
        onClassTransformation: ClassTransformation,
    ) {
        val anonymousAware = object : TransformAware{
            override fun preTransform() { onPreTransform?.invoke() }

            override fun postTransform() { onPostTransform?.invoke() }

            override fun getClassTransformer(): ClassTransformation = onClassTransformation
        }

        registerTransformAware(anonymousAware)
    }

    fun getClassTransformer(): ClassTransformation = { t: TransformTicket, bs: ByteArray ->
        transforms
            .map(TransformAware::getClassTransformer)
            .fold(bs) { acc: ByteArray, f: (TransformTicket, ByteArray) -> ByteArray -> f.invoke(t, acc) }
    }

}


fun Project.registerTransformAware(
    onPreTransform: (() -> Unit)? = null,
    onPostTransform: (() -> Unit)? = null,
    onClassTransformation: ClassTransformation,
) {
    afterEvaluate {
        val hasDispatcher = this.requireAppExtension.transforms.any { it is TransformDispatcher }
        if (!hasDispatcher) this.requireAppExtension.registerTransform(TransformDispatcher())
        TransformAwareManager.registerTransformAware(onPreTransform,onPostTransform,onClassTransformation)
    }
}
