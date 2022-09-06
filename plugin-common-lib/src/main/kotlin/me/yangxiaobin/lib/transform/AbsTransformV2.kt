package me.yangxiaobin.lib.transform

import com.android.build.api.transform.*
import com.android.build.api.variant.VariantInfo
import me.yangxiaobin.lib.GradleTransform
import me.yangxiaobin.lib.ext.neatName
import me.yangxiaobin.lib.log.LogAware
import java.io.File


open class AbsTransformV2(private val logDelegate: LogAware) : GradleTransform(), LogAware by logDelegate {

    override val LOG_TAG: String get() = "AbsTransformV2"

    override fun getName(): String = this.neatName

    /**
     * 当前 Transform 可以消费的 scope 类型
     *
     * Java class or java resource
     */
    override fun getInputTypes() = setOf(QualifiedContent.DefaultContentType.CLASSES)

    /**
     * 当前 Transform 可以生产的 scope 类型

     * Java class or java resource
     */
    override fun getOutputTypes(): MutableSet<QualifiedContent.ContentType> = super.getOutputTypes()


    /**
     * 当前 Transform 要消费哪个作用域的文件, 默认使用当前 Project 文件，包含 sub projects
     *
     * @see QualifiedContent.Scope
     */
    override fun getScopes(): MutableSet<QualifiedContent.ScopeType> = mutableSetOf(QualifiedContent.Scope.PROJECT)


    /**
     * 默认支持增量
     */
    override fun isIncremental(): Boolean = true

    @Suppress("UnstableApiUsage")
    override fun applyToVariant(variant: VariantInfo?): Boolean = super.applyToVariant(variant)


    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        val impl = GradleTransformImpl(transformInvocation)
        impl.preTransform()
        impl.doTransform(transformInvocation.asInput())
        impl.postTransform()
    }

    /**
     * 核心逻辑
     */
    private fun TransformInvocation.asInput(): TransformMaterials {

        fun getDestJar(jarInput: JarInput): File = outputProvider.getContentLocation(
            jarInput.name,
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )

        fun getDestDir(directoryInput: DirectoryInput): File = outputProvider.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes,
            directoryInput.scopes,
            Format.DIRECTORY
        )

        val copyTransformer = FileCopyTransformer(logDelegate)
        val jarTransformer = JarFileTransformer(logDelegate)
        val classTransformer = ClassFileTransformer(logDelegate)

        val wholeInputs = this.inputs.asSequence()

        val jarsEntries: List<TransformEntry> = wholeInputs
            .flatMap { it.jarInputs }
            .map { jar: JarInput -> jar.file to getDestJar(jar) }
            .map { TransformEntry(it.first, it.second, copyTransformer::transform) }
            .toList()

        val dirEntries = wholeInputs
            .flatMap { it.directoryInputs }
            .map { dir: DirectoryInput -> dir.file to getDestDir(dir) }
            .map { TransformEntry(it.first, it.second, copyTransformer::transform) }
            .toList()

        return jarsEntries + dirEntries
    }

    override fun logV(message: String) {
        super.logV(message)
    }

    override fun logI(message: String) {
        super.logI(message)
    }

    override fun logD(message: String) {
        super.logD(message)
    }

    override fun logE(message: String) {
        super.logE(message)
    }
}
