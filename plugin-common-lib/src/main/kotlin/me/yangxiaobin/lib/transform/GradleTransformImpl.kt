package me.yangxiaobin.lib.transform

import com.android.build.api.transform.TransformInvocation
import me.yangxiaobin.lib.ext.isClassFile
import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.log.LogDelegate
import java.io.File


private const val LOG_TAG = "TransformAwareImpl"
private val defaultLogDelegate = LogDelegate(InternalLogger, LOG_TAG)

class GradleTransformImpl(
    private val invocation: TransformInvocation,
    private val logDelegate: LogAware = defaultLogDelegate,
) :
    TransformAware, LogAware by logDelegate {

    override fun preTransform() {
        if (!invocation.isIncremental) invocation.outputProvider.deleteAll()
    }

    override fun doTransform(materials: TransformMaterials) {

        materials.forEach { logI("dispatch material : ${it.input}, ${it.output}.") }

        // TODO
        val engine: TransformEngine = ThreadExecutorEngine()

        val copyTransformer = FileCopyTransformer()
        val classTransformer = ClassFileTransformer()
        val jarTransformer = JarFileTransformer()

        materials.forEach { entry: TransformEntry ->
            when (entry) {
                is DeleteTransformEntry -> entry.input.delete()

                is JarTransformEntry -> {
                    jarTransformer.transform(entry.input, entry.output)
                }

                is DirTransformEntry -> {
                   // copyTransformer.transform(entry.input, entry.output)
                    entry.input.walkTopDown().forEach { f: File ->

                        val outputFile = File(entry.output, f.relativeTo(entry.input).path)

                        /*println(
                            """
                            walk down, cur file :$f
                            entry input :${entry.input}
                            entry output: ${entry.output}
                            relativePath: ${f.relativeTo(entry.output).path}
                            output: $outputFile
                            ${"\r\n"}
                        """.trimIndent()
                        )*/

                        if (f.isClassFile()) classTransformer.transform(f, outputFile)
                        else copyTransformer.transform(f, outputFile)

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
