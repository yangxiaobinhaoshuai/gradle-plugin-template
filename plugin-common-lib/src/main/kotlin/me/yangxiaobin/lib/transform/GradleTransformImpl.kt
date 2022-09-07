package me.yangxiaobin.lib.transform

import com.android.build.api.transform.TransformInvocation
import me.yangxiaobin.lib.ext.isClassFile
import java.io.File


class GradleTransformImpl(private val invocation: TransformInvocation) : TransformAware {

    override fun preTransform() {
        if (!invocation.isIncremental) invocation.outputProvider.deleteAll()
    }

    override fun doTransform(materials: TransformMaterials) {

        materials.forEach {
            println("---> material : ${it.input} , ${it.output}")
        }

        // TODO
        val engine: TransformEngine = ThreadExecutorEngine()

        val copyTransformer = FileCopyTransformer()
        val classTransformer = ClassFileTransformer()

        materials.forEach { entry: TransformEntry ->
            when (entry) {
                is DeleteTransformEntry -> entry.input.delete()

                is JarTransformEntry -> {
                    copyTransformer.transform(entry.input, entry.output)
                }

                is DirTransformEntry -> {
                   // copyTransformer.transform(entry.input, entry.output)
                    entry.input.walkTopDown().forEach { f: File ->

                        val outputFile = File(entry.output, f.relativeTo(entry.input).path)

                        println(
                            """
                            walk down, cur file :$f
                            entry input :${entry.input}
                            entry output: ${entry.output}
                            relativePath: ${f.relativeTo(entry.output).path}
                            output: $outputFile
                            ${"\r\n"}
                        """.trimIndent()
                        )

                        if (f.isClassFile()) {
                            classTransformer.transform(f, outputFile)
                        } else {
                            copyTransformer.transform(f, outputFile)
                        }

                    }
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
