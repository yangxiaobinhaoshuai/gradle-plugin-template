package me.yangxiaobin.lib.transform

import com.android.build.api.transform.TransformInvocation
import me.yangxiaobin.lib.TransformAction
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
) : TransformAware, LogAware by logDelegate {

    override fun preTransform() {
        if (!invocation.isIncremental) invocation.outputProvider.deleteAll()
    }

    override fun doTransform(materials: TransformMaterials) {

        //materials.forEach { logI("dispatch material : ${it.input}, ${it.output}.") }

        val engine: TransformEngine = ThreadExecutorEngine()

        val actions = mapEntryToAction(materials)

        engine.submitTransformAction(actions)
    }

    private fun mapEntryToAction(materials: TransformMaterials): List<TransformAction> {

        val copyTransformer = FileCopyTypeTransformer(logDelegate)
        val classTransformer = ClassFileTypeTransformer(logDelegate)
        val jarTransformer = JarFileTypeTransformer(logDelegate)

        val actions: List<TransformAction> = materials.map { entry: TransformEntry ->

            when (entry) {

                is DeleteTransformEntry -> listOf(TransformAction { entry.output.delete() })

                is JarTransformEntry -> listOf(TransformAction { jarTransformer.syncTransform(entry.input, entry.output) })

                is DirTransformEntry -> {

                    entry.input.walkTopDown().map { childFile: File ->

                        val outputFile = File(entry.output, childFile.relativeTo(entry.input).path)

                        /*println(
                            """
                            walk down, cur file :$childFile
                            entry input :${entry.input}
                            entry output: ${entry.output}
                            relativePath: ${childFile.relativeTo(entry.output).path}
                            output: $outputFile
                            ${"\r\n"}
                        """.trimIndent()
                        )*/

                        if (childFile.isClassFile()) TransformAction { classTransformer.syncTransform(childFile, outputFile) }
                        else TransformAction { copyTransformer.syncTransform(childFile, outputFile) }

                    }.toList()

                }
            }
        }.flatten()

        return actions

    }

    override fun postTransform() {

    }
}
