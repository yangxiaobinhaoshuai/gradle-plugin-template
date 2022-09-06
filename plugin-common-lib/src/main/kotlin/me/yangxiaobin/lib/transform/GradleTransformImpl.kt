package me.yangxiaobin.lib.transform

import com.android.build.api.transform.TransformInvocation

class GradleTransformImpl(private val invocation: TransformInvocation) : TransformAware {

    override fun preTransform() {
        if (!invocation.isIncremental) invocation.outputProvider.deleteAll()
    }

    override fun doTransform(input: TransformInput) {

        input.forEach { (input, output, transform) ->
            transform.invoke(input, output)
        }

    }

    override fun postTransform() {

    }
}
