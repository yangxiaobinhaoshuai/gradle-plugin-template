package me.yangxiaobin.lib.transform

import com.android.build.api.transform.TransformInvocation

/**
 * 和 Gradle Transform api 强相关
 */
class GradleTransformImpl(private val invocation: TransformInvocation) : TransformAware {

    override fun preTransform() {
        if (!invocation.isIncremental) invocation.outputProvider.deleteAll()
    }

    override fun doTransform(matrials: TransformMaterials) {
        val engine = ThreadExecutorEngine()
        matrials.forEach(engine::submitTransformEntry)
    }

    override fun postTransform() {

    }
}
