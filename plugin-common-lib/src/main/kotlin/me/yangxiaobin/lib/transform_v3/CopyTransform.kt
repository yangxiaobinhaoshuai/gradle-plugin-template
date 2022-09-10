package me.yangxiaobin.lib.transform_v3

import com.android.build.api.transform.JarInput
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformInvocation
import me.yangxiaobin.lib.log.LogAware
import java.io.File

open class CopyTransform(d: LogAware) : AbsGradleTransform(d) {

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        val outputProvider = transformInvocation.outputProvider

        if (transformInvocation.isIncremental){

            transformInvocation.inputs
                .map { it.jarInputs }
                .flatten()
                .forEach { jarInput: JarInput ->
                    when (jarInput.status) {
                        Status.NOTCHANGED -> Unit
                        Status.ADDED, Status.CHANGED -> jarInput.file.copyTo(getInputJarDestFile(jarInput.file,outputProvider))
                        Status.REMOVED -> getInputJarDestFile(jarInput.file, outputProvider).delete()
                        else -> Unit
                    }
                }

            transformInvocation.inputs
                .map { it.directoryInputs }
                .flatten()
                .map { it.changedFiles }
                .flatMap { it.entries }
                .forEach { (file,status)->
                    when (status) {
                        Status.NOTCHANGED -> Unit
                        Status.ADDED, Status.CHANGED -> file.copyRecursively(File(getInputDirDestDir(file, outputProvider),file.name))
                        Status.REMOVED -> File(getInputDirDestDir(file, outputProvider),file.name).delete()
                        else -> Unit
                    }
                }


        } else {

            transformInvocation.outputProvider.deleteAll()

            transformInvocation.inputs
                .map { it.jarInputs }
                .flatten()
                .forEach {jarInput: JarInput ->
                    jarInput.file.copyTo( getInputJarDestFile(jarInput.file, outputProvider))
                }


            transformInvocation.inputs
                .map { it.directoryInputs }
                .flatten()
                .map { it.file }
                .forEach {
                    val dirInputDestFile = File(getInputDirDestDir(it, outputProvider), it.name)
                    it.copyRecursively(dirInputDestFile)
                }
        }


    }
}
