package me.yangxiaobin.lib.transform

import com.android.build.api.transform.*
import kotlinx.coroutines.*
import me.yangxiaobin.lib.coroutine.coroutineHandler
import me.yangxiaobin.lib.coroutine.transportCoroutineName
import me.yangxiaobin.lib.ext.*
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import me.yangxiaobin.lib.thread.TransformThreadFactory
import org.gradle.api.Project
import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.function.Function
import java.util.zip.ZipFile


open class AbsLegacyTransform(protected val project: Project) : Transform() {

    protected val logger = Logger.copy().setLevel(LogLevel.DEBUG)

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


    /**
     * FixedThreadPoolExecutor
     */
    private val transportExecutor = ThreadPoolExecutor(
        2 * CPU_COUNT, 2 * CPU_COUNT,
        0L, TimeUnit.MICROSECONDS,
        LinkedBlockingQueue(),
        TransformThreadFactory()
    )

    private val transportScope: CoroutineScope by lazy {
        CoroutineScope(
            transportExecutor.asCoroutineDispatcher()
                    + SupervisorJob()
                    + coroutineHandler
                    + transportCoroutineName
        )
    }

    private val jarFileTransformer: Function<ByteArray, ByteArray>? by lazy { getJarTransformer() }
    private val classFileTransformer: Function<ByteArray, ByteArray>? by lazy { getClassTransformer() }

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

        invocation.inputs.forEach { transformInput: TransformInput ->

            // 1. Process vendor jars.
            processJarInput(transformInput.jarInputs, invocation)

            // 2. Process source classes.
            processClassFile(transformInput.directoryInputs, invocation)
        }

        afterTransform()

        logI("${invocation.context.variantName} transform ends in ${(System.currentTimeMillis() - t1).toFormat(false)}")
    }

    private fun processJarInput(
        jarInputs: Collection<JarInput>,
        invocation: TransformInvocation
    ) {
        jarInputs.forEach { jarInput: JarInput ->

            val outputJar: File = invocation.outputProvider.getContentLocation(
                jarInput.name,
                jarInput.contentTypes,
                jarInput.scopes,
                Format.JAR
            )

            if (invocation.isIncremental) {
                when (jarInput.status) {
                    Status.ADDED, Status.CHANGED -> transportJarFile(jarInput.file, outputJar)
                    Status.REMOVED -> outputJar.delete()
                    Status.NOTCHANGED -> {
                        // No need to transform.
                    }
                    else -> {
                        error("Unknown status: ${jarInput.status}")
                    }
                }
            } else {
                transportJarFile(jarInput.file, outputJar)
            }
        }
    }

    private fun processClassFile(
        directoryInputs: Collection<DirectoryInput>,
        invocation: TransformInvocation
    ) {

        fun toOutputFile(outputDir: File, inputDir: File, inputFile: File) =
            File(outputDir, inputFile.relativeTo(inputDir).path)

        directoryInputs.forEach { directoryInput: DirectoryInput ->

            val outputDir = invocation.outputProvider.getContentLocation(
                directoryInput.name,
                directoryInput.contentTypes,
                directoryInput.scopes,
                Format.DIRECTORY
            )

            if (invocation.isIncremental) {
                directoryInput.changedFiles.forEach { (changedFile, status) ->

                    val outputFile = toOutputFile(outputDir, directoryInput.file, changedFile)

                    when (status) {
                        Status.ADDED, Status.CHANGED -> transportClassFile(changedFile, outputFile.parentFile)
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
                    transportClassFile(file, outputFile.parentFile)
                }
            }
        }
    }

    private fun transportJarFile(inputJarFile: File, outputJarFile: File) {

        // We are only interested in project compiled classes but we have to copy received jars to the
        // output.
        fun copyJar(inputJar: File, outputJar: File) = inputJar.copyTo(target = outputJar.touch(), overwrite = true)

        when {
            jarFileTransformer == null -> transportScope.launch {  copyJar(inputJarFile, outputJarFile)}

            inputJarFile.isJarFile() && isJarValid(inputJarFile) -> {

                logD(" transforming jar: ${inputJarFile.name}")

                // 1. Unzip jar.
                // 2. Do transformation.
                // 3. Write jar.

                ZipFile(inputJarFile).parallelTransformTo(outputJarFile, jarFileTransformer!!::apply)
            }

            inputJarFile.isFile ->  transportScope.launch { copyJar(inputJarFile, outputJarFile)}
        }
    }

    // Transform a single file. If the file is not a class file it is just copied to the output dir.
    private fun transportClassFile(inputFile: File, outputDir: File) {

        outputDir.mkdirs()
        val outputFile = File(outputDir, inputFile.name)

        fun copyClassFile() {
            // Copy all non .class files to the output.
            inputFile.copyTo(target = outputFile, overwrite = true)
        }

        when {
            classFileTransformer == null -> transportScope.launch { copyClassFile() }

            inputFile.isClassFile() && isClassValid(inputFile) -> {

                logD("transforming class file: ${inputFile.name}")

                transportScope.launch {

                    val transformedByteArr: ByteArray = inputFile.readBytes().let(classFileTransformer!!::apply)

                    outputFile.writeBytes(transformedByteArr)
                }
            }

            // Copy all non .class files to the output.
            inputFile.isFile -> transportScope.launch { copyClassFile() }
        }
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

}
