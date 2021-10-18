package me.yangxiaobin.lib.transform

import com.android.build.api.transform.*
import me.yangxiaobin.lib.ext.*
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import java.io.File
import java.util.zip.ZipFile


open class AbsLegacyTransform : Transform() {

    protected val logger = Logger.copy().setLevel(LogLevel.INFO)

    protected val logI = logger.log(LogLevel.INFO, name)

    protected val logV = logger.log(LogLevel.VERBOSE,name)

    override fun getInputTypes() = setOf(QualifiedContent.DefaultContentType.CLASSES)

    override fun getName(): String = "AbsLegacyTransform"

    override fun isIncremental() = true

    override fun getScopes(): MutableSet<QualifiedContent.ScopeType> = mutableSetOf(QualifiedContent.Scope.PROJECT)

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
            transformInput.jarInputs.forEach { jarInput: JarInput ->

                logV("input jar file :${jarInput.file.name}")

                val outputJar: File =
                    invocation.outputProvider.getContentLocation(
                        jarInput.name,
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR
                    )

                val jarTransformer: ByteCodeTransformer? = getJarTransformer()

                if (invocation.isIncremental) {
                    when (jarInput.status) {
                        Status.ADDED, Status.CHANGED -> transformJarFile(jarInput.file, outputJar, jarTransformer)
                        Status.REMOVED -> outputJar.delete()
                        Status.NOTCHANGED -> {
                            // No need to transform.
                        }
                        else -> {
                            error("Unknown status: ${jarInput.status}")
                        }
                    }
                } else {
                    transformJarFile(jarInput.file, outputJar, jarTransformer)
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
                val classTransformer = getClassTransformer()

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
                        logV("input class file  :${file.name}")
                        val outputFile = toOutputFile(outputDir, directoryInput.file, file)
                        transformClassFile(file, outputFile.parentFile, classTransformer)
                    }
                }
            }
        }

        logI("${invocation.context.variantName} transform ends in ${(System.currentTimeMillis() - t1).toFormat(false)}")
    }


    protected open fun getClassTransformer(): ByteCodeTransformer = DefaultByteCodeTransformer()

    protected open fun getJarTransformer(): ByteCodeTransformer? = null

    // Transform a single file. If the file is not a class file it is just copied to the output dir.
    private fun transformClassFile(
        inputFile: File,
        outputDir: File,
        transformer: ByteCodeTransformer
    ) {
        if (inputFile.isClassFile()) {

            val transformedByteArr = inputFile.readBytes().let(transformer::transformByteArray)

            outputDir.mkdirs()
            val outputFile = File(outputDir, inputFile.name)
            outputFile.writeBytes(transformedByteArr)

        } else if (inputFile.isFile) {
            // Copy all non .class files to the output.
            outputDir.mkdirs()
            val outputFile = File(outputDir, inputFile.name)
            inputFile.copyTo(target = outputFile, overwrite = true)
        }
    }

    private fun transformJarFile(
        inputJarFile: File,
        outputJarFile: File,
        transformer: ByteCodeTransformer?
    ) {
        if (transformer == null) {
            copyJar(inputJarFile, outputJarFile)
            return
        }

        if (inputJarFile.isJarFile()) {
            // 1. Unzip jar.
            // 2. Do transformation.
            // 3. Write jar.

            ZipFile(inputJarFile.touch()).parallelTransformTo(outputJarFile,transformer::transformByteArray)

        } else if (inputJarFile.isFile) {
            copyJar(inputJarFile, outputJarFile)
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
