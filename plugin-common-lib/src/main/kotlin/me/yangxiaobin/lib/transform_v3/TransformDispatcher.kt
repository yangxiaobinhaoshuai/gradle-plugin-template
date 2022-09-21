@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION")

package me.yangxiaobin.lib.transform_v3

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import me.yangxiaobin.lib.GradleTransformStatus
import me.yangxiaobin.lib.ext.isJarFile
import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.log.LogDelegate
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

private val defaultLogAware = LogDelegate(InternalLogger, "TransformDispatcher")

open class TransformDispatcher(d: LogAware = defaultLogAware) : AbsGradleTransform(d), TransformAware {

    // TODO di here.
    private val transformBus: TransformBus by lazy { TransformTicketImpl }

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        val ts =  TransformAwareManager.transforms

        FunctionInvoker.register(FunctionKey.of("preTransform")) { ts.forEach(TransformAware::preTransform) }
            .register(FunctionKey.of("dispatchTransform")) { dispatchInput(transformInvocation) }
            .register(FunctionKey.of("postTransform")) { ts.forEach(TransformAware::postTransform) }
            .start()
    }

    override fun preTransform() {

    }

    override fun postTransform() {

    }

    // TODO
    override fun getClassTransformer(): ClassTransformation = { _, bs ->
        val cr = ClassReader(bs)

        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)

        val cv = object : ClassVisitor(Opcodes.ASM7, cw) {

            override fun visit(
                version: Int,
                access: Int,
                name: String?,
                signature: String?,
                superName: String?,
                interfaces: Array<out String>?
            ) {
                super.visit(version, access, name, signature, superName, interfaces)
                //println("----> default visit class :$name.")
            }
        }

        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        cw.toByteArray()
        //bs
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
                .map { DeleteTicket(it, getInputDirDestDir(it, outputProvider)) }

            val changedDirs = context.inputs.flatMap { it.directoryInputs }
                .map { it.changedFiles }
                .flatMap { it.entries }
                .filter { it.value == GradleTransformStatus.ADDED || it.value == GradleTransformStatus.CHANGED }
                .map { it.key }
                .map { ChangedFileTicket(it, getInputDirDestDir(it, outputProvider)) }

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

        transformBus.takeTickets(tickets)
    }

}
