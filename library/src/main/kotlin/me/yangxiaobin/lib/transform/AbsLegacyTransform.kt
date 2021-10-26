package me.yangxiaobin.lib.transform

import com.android.build.api.transform.*
import me.yangxiaobin.lib.ext.*
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.gradle.api.Project
import java.io.File
import java.util.function.Function
import java.util.zip.ZipFile


open class AbsLegacyTransform(protected val project: Project) : Transform() {

    protected val logger = Logger.copy().setLevel(LogLevel.INFO)

    protected val logE = logger.log(LogLevel.ERROR, name)

    protected val logD = logger.log(LogLevel.DEBUG, name)

    protected val logI = logger.log(LogLevel.INFO, name)

    protected val logV = logger.log(LogLevel.VERBOSE, name)

    var currentVariantName: String? = null
        private set

    override fun getInputTypes() = setOf(QualifiedContent.DefaultContentType.CLASSES)

    override fun getName(): String = "AbsLegacyTransform"

    override fun isIncremental() = true

    override fun getScopes(): MutableSet<QualifiedContent.ScopeType> = mutableSetOf(QualifiedContent.Scope.PROJECT)

    override fun transform(invocation: TransformInvocation) {

        val t1 = System.currentTimeMillis()
        logI("variant: ${invocation.context.variantName}(isIncremental:${invocation.isIncremental}) transform begins.")
        currentVariantName = invocation.context.variantName

        beforeTransform()

        if (!invocation.isIncremental) {
            // Remove any lingering files on a non-incremental invocation since everything has to be
            // transformed.
            invocation.outputProvider.deleteAll()
        }

        invocation.inputs.forEach { transformInput ->

            // 1. Process vendor jars.
            transformInput.jarInputs.forEach { jarInput: JarInput ->

                val outputJar: File = invocation.outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )

                val jarTransformer = getJarTransformer()

                if (invocation.isIncremental) {
                    when (jarInput.status) {
                        Status.ADDED, Status.CHANGED -> transformJarFile(
                            jarInput.file,
                            outputJar,
                            jarTransformer,
                            jarInput.status
                        )
                        Status.REMOVED -> outputJar.delete()
                        Status.NOTCHANGED -> {
                            // No need to transform.
                        }
                        else -> {
                            error("Unknown status: ${jarInput.status}")
                        }
                    }
                } else {
                    transformJarFile(jarInput.file, outputJar, jarTransformer, jarInput.status)
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
                            Status.ADDED, Status.CHANGED -> transformClassFile(
                                file,
                                outputFile.parentFile,
                                classTransformer,
                                status
                            )
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
                        transformClassFile(file, outputFile.parentFile, classTransformer, null)
                    }
                }
            }
        }

        afterTransform()

        logI("${invocation.context.variantName} transform ends in ${(System.currentTimeMillis() - t1).toFormat(false)}")
    }


    protected open fun getClassTransformer(): Function<ByteArray, ByteArray>? = DefaultByteCodeTransformer()

    protected open fun getJarTransformer(): Function<ByteArray, ByteArray>? = null

    protected open fun beforeTransform() {}

    protected open fun afterTransform() {}

    /**
     * Black list array.
     */
    protected open fun isClassValid(f: File): Boolean = arrayOf("BuildConfig.class")
        .fold(true) { acc: Boolean, regex: String -> acc && !regex.toRegex().matches(f.name) }

    /**
     * Black list array.
     */
    protected open fun isJarValid(jar: File): Boolean = arrayOf(
        "R.jar",
        "annotation-.+.jar",
        "jetified-annotations-.+.jar",
    ).fold(true) { acc: Boolean, regex: String -> acc && !regex.toRegex().matches(jar.name) }


    // Transform a single file. If the file is not a class file it is just copied to the output dir.
    private fun transformClassFile(
        inputFile: File,
        outputDir: File,
        transformer: Function<ByteArray, ByteArray>?,
        status: Status?,
    ) {

        fun copyClassFile() {
            // Copy all non .class files to the output.
            outputDir.mkdirs()
            val outputFile = File(outputDir, inputFile.name)
            inputFile.copyTo(target = outputFile, overwrite = true)
        }

        when {
            transformer == null -> copyClassFile()

            inputFile.isClassFile() && isClassValid(inputFile) -> {
                logV("transforming class file: ${inputFile.name}, incremental status :$status")

                val transformedByteArr = inputFile.readBytes().let(transformer::apply)

                outputDir.mkdirs()
                val outputFile = File(outputDir, inputFile.name)
                outputFile.writeBytes(transformedByteArr)
            }

            // Copy all non .class files to the output.
            inputFile.isFile -> copyClassFile()
        }
    }

    private fun transformJarFile(
        inputJarFile: File,
        outputJarFile: File,
        transformer: Function<ByteArray, ByteArray>?,
        status: Status,
    ) {
        when {
            transformer == null -> copyJar(inputJarFile, outputJarFile)

            inputJarFile.isJarFile() && isJarValid(inputJarFile) -> {

                logV("transforming jar: ${inputJarFile.name}, incremental status :$status")

                // 1. Unzip jar.
                // 2. Do transformation.
                // 3. Write jar.

                ZipFile(inputJarFile).parallelTransformTo(outputJarFile, transformer::apply)
            }

            inputJarFile.isFile -> copyJar(inputJarFile, outputJarFile)
        }
    }


    // We are only interested in project compiled classes but we have to copy received jars to the
    // output.
    private fun copyJar(inputJar: File, outputJar: File) = inputJar.copyTo(target = outputJar.touch(), overwrite = true)


    private fun toOutputFile(outputDir: File, inputDir: File, inputFile: File) =
        File(outputDir, inputFile.relativeTo(inputDir).path)
}
