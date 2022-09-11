package me.yangxiaobin.lib.transform_v3

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import me.yangxiaobin.lib.GradleTransformStatus
import me.yangxiaobin.lib.ext.isJarFile
import me.yangxiaobin.lib.log.LogAware

@Suppress("TYPEALIAS_EXPANSION_DEPRECATION")
open class BaseTransformV3(d: LogAware) : AbsGradleTransform(d) {

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        dispatchInput(transformInvocation)

        FunctionInvoker.register(FunctionKey.of("preTransform")) {}
            .register(FunctionKey.of("preTransform")) { }
            .register(FunctionKey.of("transform")) { }
            .register(FunctionKey.of("postTransform")) { }
            .start()
    }

    private fun dispatchInput(context: TransformInvocation){

        val outputProvider = context.outputProvider

       val tickets: List<TransformTicket> = if (context.isIncremental) {

            val  deleteJars = context.inputs.flatMap { it.jarInputs}
                .filter { it.status == GradleTransformStatus.REMOVED }
                .map(QualifiedContent::getFile)
                .map { DeleteTicket(it, getInputJarDestFile(it, outputProvider)) }

            val  changedJars = context.inputs.flatMap { it.jarInputs}
                .filter { it.status == GradleTransformStatus.ADDED || it.status == GradleTransformStatus.CHANGED }
                .map(QualifiedContent::getFile)
                .map { ChangedFileTicket(it, getInputJarDestFile(it, outputProvider)) }

            val deleteDirs = context.inputs.flatMap { it.directoryInputs }
                .map { it.changedFiles }
                .flatMap { it.entries }
                .filter { it.value == GradleTransformStatus.REMOVED }
                .map { it.key }
                .map { DeleteTicket(it, getInputJarDestFile(it, outputProvider)) }

            val changedDirs = context.inputs.flatMap { it.directoryInputs }
                .map { it.changedFiles }
                .flatMap { it.entries }
                .filter { it.value == GradleTransformStatus.ADDED || it.value == GradleTransformStatus.CHANGED }
                .map { it.key }
                .map { ChangedFileTicket(it, getInputJarDestFile(it, outputProvider)) }

           deleteJars + deleteDirs + changedJars + changedDirs

        } else {

            // This matters.
            context.outputProvider.deleteAll()

            context.inputs.flatMap { it.jarInputs + it.directoryInputs }
                .map(QualifiedContent::getFile)
                .map {

                    val destFile = if (it.isJarFile())
                        getInputJarDestFile(it, outputProvider)
                    else
                        getInputDirDestDir(it, outputProvider)

                    ChangedFileTicket(it, destFile)
                }
        }

        TransformTicketImpl.takeTickets(tickets)
    }

}
