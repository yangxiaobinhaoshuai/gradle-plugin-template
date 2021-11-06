package me.yangxiaobin.lib.transform

import com.android.build.api.transform.*
import kotlinx.coroutines.*
import me.yangxiaobin.lib.coroutine.coroutineHandler
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


@Suppress("MemberVisibilityCanBePrivate")
open class AbsLegacyTransform(protected val project: Project) : Transform() {

    protected val logger = Logger.copy().setLevel(LogLevel.VERBOSE)

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
    private val transformExecutor = ThreadPoolExecutor(
        2 * CPU_COUNT, 2 * CPU_COUNT,
        0L, TimeUnit.MICROSECONDS,
        LinkedBlockingQueue(),
        TransformThreadFactory()
    )

    protected val transformScope: CoroutineScope by lazy {
        CoroutineScope(
            transformExecutor.asCoroutineDispatcher()
//            Dispatchers.IO
                    + SupervisorJob()
                    + coroutineHandler
                    + CoroutineName("Transport-Coroutine")
        )
    }

    private val transformJobs = mutableSetOf<Job>()

    private val jarFileTransformer: Function<ByteArray, ByteArray>? by lazy { getJarTransformer() }
    private val classFileTransformer: Function<ByteArray, ByteArray>? by lazy { getClassTransformer() }

    override fun transform(invocation: TransformInvocation) {

        val t1 = System.currentTimeMillis()
        logI("Transform : ${invocation.context.variantName}(incremental:${invocation.isIncremental}) begins.")
        currentVariantName = invocation.context.variantName

        beforeTransform()

        if (!invocation.isIncremental) {
            // Remove any lingering files on a non-incremental invocation since everything has to be
            @Suppress("BlockingMethodInNonBlockingContext")
            invocation.outputProvider.deleteAll()
        }

        invocation.inputs
            .flatMap { it.jarInputs + it.directoryInputs }
            .groupBy { it is JarInput }
            .forEach { (isJar: Boolean, inputList: List<QualifiedContent>) ->

                @Suppress("UNCHECKED_CAST")
                if (isJar) {
                    // 1. Process vendor jars.
                    processJarInput(inputList as List<JarInput>, invocation)
                } else {
                    // 2. Process source classes.
                    processClassFile(inputList as List<DirectoryInput>, invocation)
                }

            }

        afterTransform()

        logI("Transform : ${invocation.context.variantName} ends in ${(System.currentTimeMillis() - t1).toFormat(false)}")
    }

    private fun processJarInput(
        jarInputs: Collection<JarInput>,
        invocation: TransformInvocation
    ) {
        val t1 = System.currentTimeMillis()
        logI(" processJarFile begins.")

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
        logI(" processJarFile ends in ${(System.currentTimeMillis() - t1).toFormat(false)}")
    }

    private fun processClassFile(
        directoryInputs: Collection<DirectoryInput>,
        invocation: TransformInvocation
    ) {
        val t1 = System.currentTimeMillis()
        logI(" processClassFile begins.")

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

        logI(" processClassFile ends in ${(System.currentTimeMillis() - t1).toFormat(false)}")
    }

    private fun transportJarFile(inputJarFile: File, outputJarFile: File) {

        // We are only interested in project compiled classes but we have to copy received jars to the
        // output.
        fun copyJar(inputJar: File, outputJar: File) = inputJar.copyTo(target = outputJar.touch(), overwrite = true)

        when {
            jarFileTransformer == null -> copyJar(inputJarFile, outputJarFile)

            inputJarFile.isJarFile() && isJarValid(inputJarFile) -> {

                logD(" transforming jar: ${inputJarFile.name}")

                // 1. Unzip jar.
                // 2. Do transformation.
                // 3. Write jar.

                ZipFile(inputJarFile).simpleTransformTo(outputJarFile, jarFileTransformer!!::apply)
            }

            inputJarFile.isFile -> copyJar(inputJarFile, outputJarFile)
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
            classFileTransformer == null -> copyClassFile()

            inputFile.isClassFile() && isClassValid(inputFile) -> {

                logD("transforming class file: ${inputFile.name}")

                val transformedByteArr: ByteArray = inputFile.readBytes().let(classFileTransformer!!::apply)

                outputFile.writeBytes(transformedByteArr)
            }

            // Copy all non .class files to the output.
            inputFile.isFile -> copyClassFile()
        }
    }


    protected open fun getClassTransformer(): Function<ByteArray, ByteArray>? = null

    protected open fun getJarTransformer(): Function<ByteArray, ByteArray>? = null

    protected open fun beforeTransform() {}

    protected open fun afterTransform() {}

    /**
     * Black list array.
     */
    protected open fun isClassValid(f: File): Boolean = true

    /**
     * Black list array.
     */
    protected open fun isJarValid(jar: File): Boolean = true

}
