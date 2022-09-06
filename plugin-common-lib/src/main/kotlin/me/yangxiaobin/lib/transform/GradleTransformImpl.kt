package me.yangxiaobin.lib.transform

import com.android.build.api.transform.TransformInvocation


class GradleTransformImpl(private val invocation: TransformInvocation) : TransformAware {

    override fun preTransform() {
        if (!invocation.isIncremental) invocation.outputProvider.deleteAll()
    }

    override fun doTransform(materials: TransformMaterials) {

        // TODO
        val engine: TransformEngine = ThreadExecutorEngine()

        val copyTransformer = FileCopyTransformer()

        materials.forEach { entry: TransformEntry ->
            when (entry) {
                is DeleteTransformEntry -> entry.input.delete()

                is JarTransformEntry -> {
                    copyTransformer.transform(entry.input, entry.output)
                }

                is DirTransformEntry -> {
                    copyTransformer.transform(entry.input, entry.output)
                }
            }
        }

    }

    private fun dispatchMaterials(materials: TransformMaterials) {

        materials.filterIsInstance<JarTransformEntry>()

        materials.filterIsInstance<DirTransformEntry>()
    }

    override fun postTransform() {

    }
}
