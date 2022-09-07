package me.yangxiaobin.lib.transform

import com.android.build.api.transform.*
import kotlinx.coroutines.*
import me.yangxiaobin.lib.ext.*
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.innerLogImpl
import me.yangxiaobin.lib.log.log
import me.yangxiaobin.lib.thread.TransformThreadFactory
import org.gradle.api.Project
import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.function.Function
import java.util.zip.ZipFile


private typealias Action = () -> Unit

/**
 * @see com.google.dagger:hilt-android-gradle-plugin:2.28-alpha
 * @see https://github.com/google/dagger/tree/master/java/dagger/hilt/android/plugin/main/src/main/kotlin/dagger/hilt/android/plugin
 */
@Suppress("MemberVisibilityCanBePrivate")
@Deprecated("Api representation, not using purpose, see AbsTransformV2")
open class AbsLegacyTransform(protected val project: Project) : Transform() {

    //TODO level 是否生效？
    protected val logger = innerLogImpl.setLevel(LogLevel.VERBOSE)

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
        0L, TimeUnit.MILLISECONDS,
        LinkedBlockingQueue(),
        TransformThreadFactory()
    )

    protected val transformScope: CoroutineScope by lazy {
        CoroutineScope(
            transformExecutor.asCoroutineDispatcher()
                    + SupervisorJob()
                    + CoroutineName("AJC-Transform")
        )
    }

    private val transformActions = mutableSetOf<Action>()

    private val jarFileTransformer: Function<ByteArray, ByteArray>? by lazy { getJarTransformer() }
    private val classFileTransformer: Function<ByteArray, ByteArray>? by lazy { getClassTransformer() }

    override fun transform(invocation: TransformInvocation) {

        val t1 = System.currentTimeMillis()
        logI("Transform : ${invocation.context.variantName}(incremental:${invocation.isIncremental}) begins.")
        currentVariantName = invocation.context.variantName

        beforeTransform()

        if (!invocation.isIncremental) {
            // Remove any lingering files on a non-incremental invocation since everything has to be
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

        val t2 = System.currentTimeMillis()
        logI("async transform action begins.")
        transformScope.launch(Dispatchers.IO) { transformActions.map { launch { it.invoke() } }.joinAll() }
            .invokeOnCompletion { logI("async transform actions ends in ${(System.currentTimeMillis() - t2).toFormat(false)}, th : $it.") }

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
                    // No need to transform.
                    Status.NOTCHANGED -> Unit
                    else -> error("Unknown status: ${jarInput.status}")
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

        fun toOutputFile(outputDir: File, inputDir: File, inputFile: File) = File(outputDir, inputFile.relativeTo(inputDir).path)

        directoryInputs.forEach { directoryInput: DirectoryInput ->

            val outputDir: File = invocation.outputProvider.getContentLocation(
                directoryInput.name,
                directoryInput.contentTypes,
                directoryInput.scopes,
                Format.DIRECTORY
            )

            val rootDir: File = directoryInput.file

            if (invocation.isIncremental) {
                directoryInput.changedFiles.forEach { (changedFile: File, status: Status) ->

                    val outputFile = toOutputFile(outputDir, rootDir, changedFile)

                    when (status) {
                        Status.ADDED, Status.CHANGED -> transportClassFile(changedFile, outputFile.parentFile)
                        Status.REMOVED -> outputFile.delete()
                        // No need to transform.
                        Status.NOTCHANGED -> Unit
                        else -> error("Unknown status: $status")
                    }
                }
            } else {

                rootDir.walkTopDown().forEach { file ->
                    val outputFile = toOutputFile(outputDir, rootDir, file)
                    transportClassFile(file, outputFile)
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

                transformActions += {
                    ZipFile(inputJarFile).simpleTransformTo(
                        outputJarFile,
                        jarFileTransformer!!::apply
                    )
                }
            }

            inputJarFile.isFile -> transformActions += { copyJar(inputJarFile, outputJarFile) }
        }
    }

    // Transform a single file. If the file is not a class file it is just copied to the output dir.
    private fun transportClassFile(inputFile: File, outputFile: File) {

        fun copyClassFile() {
            // Copy all non .class files to the output.
            inputFile.copyTo(target = outputFile, overwrite = true)
        }

        when {
            classFileTransformer == null -> {
                // File.copyTo 处理目录会 Tried to overwrite the destination, but failed to delete it.
                if (inputFile.isFile) transformActions += { copyClassFile() }
                else copyClassFile()
            }

            inputFile.isClassFile() && isClassValid(inputFile) -> {

                logD("transforming class file: ${inputFile.name}")

                transformActions += {

                    val transformedByteArr: ByteArray = inputFile.readBytes().let(classFileTransformer!!::apply)

                    outputFile.touch().writeBytes(transformedByteArr)
                }
            }


            // Copy all non .class files to the output.
            inputFile.isFile -> transformActions += { copyClassFile() }
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
