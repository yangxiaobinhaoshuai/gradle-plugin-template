package me.yangxiaobin.lib.transform

import com.android.build.api.transform.*
import me.yangxiaobin.lib.ext.isClassFile
import me.yangxiaobin.lib.ext.toFormat
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import java.io.File


open class AbsLegacyTransform : Transform() {

    private val logI = Logger.log(LogLevel.INFO, "AbsLegacyTransform")

    override fun getInputTypes() = setOf(QualifiedContent.DefaultContentType.CLASSES)

    override fun getName(): String = "AbsLegacyTransform"

    override fun isIncremental() = true

    override fun getScopes(): MutableSet<QualifiedContent.Scope> = mutableSetOf(QualifiedContent.Scope.PROJECT)

    override fun transform(invocation: TransformInvocation) {

        val t1 = System.currentTimeMillis()
        logI("${invocation.context.variantName} transform begins.")

        if (!invocation.isIncremental) {
            // Remove any lingering files on a non-incremental invocation since everything has to be
            // transformed.
            invocation.outputProvider.deleteAll()
        }

        invocation.inputs.forEach { transformInput ->

            // 1. Process vendor jars.
            transformInput.jarInputs.forEach { jarInput ->
                val outputJar =
                    invocation.outputProvider.getContentLocation(
                        jarInput.name,
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR
                    )

                val jarTransformer: ByteCodeTransformer? = getJarTransformer(outputJar)

                if (invocation.isIncremental) {
                    when (jarInput.status) {
                        Status.ADDED, Status.CHANGED -> jarTransformer?.transformFile(jarInput.file) ?: copyJar(jarInput.file, outputJar)
                        Status.REMOVED -> outputJar.delete()
                        Status.NOTCHANGED -> {
                            // No need to transform.
                        }
                        else -> {
                            error("Unknown status: ${jarInput.status}")
                        }
                    }
                } else {
                    jarTransformer?.transformFile(jarInput.file) ?: copyJar(jarInput.file, outputJar)
                }
            }

            // 2. Process source classes.
            transformInput.directoryInputs.forEach { directoryInput ->
                val outputDir = invocation.outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                val classTransformer =
                    createAbsClassTransformer(invocation.inputs, invocation.referencedInputs, outputDir)

                if (invocation.isIncremental) {
                    directoryInput.changedFiles.forEach { (file, status) ->
                        val outputFile = toOutputFile(outputDir, directoryInput.file, file)
                        when (status) {
                            Status.ADDED, Status.CHANGED ->
                                transformClassFile(file, outputFile.parentFile, classTransformer)
                            Status.REMOVED -> outputFile.delete()
                            Status.NOTCHANGED -> {
                                // No need to transform.
                            }
                            else -> {
                                error("Unknown status: $status")
                            }
                        }
                    }
                } else {
                    directoryInput.file.walkTopDown().forEach { file ->
                        val outputFile = toOutputFile(outputDir, directoryInput.file, file)
                        transformClassFile(file, outputFile.parentFile, classTransformer)
                    }
                }
            }
        }
        logI("${invocation.context.variantName} transform ends in ${(System.currentTimeMillis() - t1).toFormat(false)}")
    }

    // Create a transformer given an invocation inputs. Note that since this is a PROJECT scoped
    // transform the actual transformation is only done on project files and not its dependencies.
    private fun createAbsClassTransformer(
        inputs: Collection<TransformInput>,
        referencedInputs: Collection<TransformInput>,
        outputDir: File
    ): ByteCodeTransformer {
        val classFiles = (inputs + referencedInputs).flatMap { input ->
            (input.directoryInputs + input.jarInputs).map { it.file }
        }
        return AbsByteCodeTransformer(sourceRootOutputDir = outputDir,)
    }

    protected open fun getClassTransformer(outputDir: File): ByteCodeTransformer = AbsByteCodeTransformer(outputDir)

    protected open fun getJarTransformer(outputJar: File): ByteCodeTransformer? = null

    // Transform a single file. If the file is not a class file it is just copied to the output dir.
    private fun transformClassFile(
        inputFile: File,
        outputDir: File,
        transformer: ByteCodeTransformer
    ) {
        if (inputFile.isClassFile()) {
            transformer.transformFile(inputFile)
        } else if (inputFile.isFile) {
            // Copy all non .class files to the output.
            outputDir.mkdirs()
            val outputFile = File(outputDir, inputFile.name)
            inputFile.copyTo(target = outputFile, overwrite = true)
        }
    }


    // We are only interested in project compiled classes but we have to copy received jars to the
    // output.
    private fun copyJar(inputJar: File, outputJar: File) {
        outputJar.parentFile?.mkdirs()
        inputJar.copyTo(target = outputJar, overwrite = true)
    }


    private fun toOutputFile(outputDir: File, inputDir: File, inputFile: File) =
        File(outputDir, inputFile.relativeTo(inputDir).path)
}
