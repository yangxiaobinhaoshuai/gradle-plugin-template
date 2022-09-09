package me.yangxiaobin.lib.transform_v3

import com.android.build.api.transform.*
import me.yangxiaobin.lib.GradleTransformStatus
import me.yangxiaobin.lib.ext.isJarFile
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.log.LogDelegate
import java.io.File

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
                .map { DeleteTicket(it,outputProvider.getDestJarFile(it.name)) }

            val  changedJars = context.inputs.flatMap { it.jarInputs}
                .filter { it.status == GradleTransformStatus.ADDED || it.status == GradleTransformStatus.CHANGED }
                .map(QualifiedContent::getFile)
                .map { ChangedFileTicket(it,outputProvider.getDestJarFile(it.name)) }

            val deleteDirs = context.inputs.flatMap { it.directoryInputs }
                .map { it.changedFiles }
                .flatMap { it.entries }
                .filter { it.value == GradleTransformStatus.REMOVED }
                .map { it.key }
                .map { DeleteTicket(it,outputProvider.getDestDirFile(it.name)) }

            val changedDirs = context.inputs.flatMap { it.directoryInputs }
                .map { it.changedFiles }
                .flatMap { it.entries }
                .filter { it.value == GradleTransformStatus.ADDED || it.value == GradleTransformStatus.CHANGED }
                .map { it.key }
                .map { ChangedFileTicket(it,outputProvider.getDestDirFile(it.name)) }

           deleteJars + deleteDirs + changedJars + changedDirs

        } else {

            context.inputs.flatMap { it.jarInputs + it.directoryInputs }
                .map(QualifiedContent::getFile)
                .map {
                    val destFile = if (it.isJarFile()) outputProvider.getDestJarFile(it.name) else outputProvider.getDestDirFile(it.name)
                    ChangedFileTicket(it, destFile)
                }
        }

        TransformTicketImpl.takeTickets(tickets)
    }

    private fun TransformOutputProvider.getDestJarFile(rawJarName: String): File = this.getContentLocation(rawJarName, inputTypes, scopes, Format.JAR)

    private fun TransformOutputProvider.getDestDirFile(rawDirName: String): File = this.getContentLocation(rawDirName, inputTypes, scopes, Format.DIRECTORY)

}
