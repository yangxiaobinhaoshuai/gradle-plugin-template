package me.yangxiaobin.lib.transform

import com.android.build.api.transform.*
import com.android.build.api.variant.VariantInfo
import me.yangxiaobin.lib.GradleTransform
import me.yangxiaobin.lib.GradleTransformStatus
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

        val impl = GradleTransformImpl(transformInvocation, this)
        impl.preTransform()
        impl.doTransform(transformInvocation.toMaterials())
        impl.postTransform()
    }

    /**
     * 分 jars 和 dirs 两大类
     */
    private fun TransformInvocation.toMaterials(): TransformMaterials {

        /**
         * 获取 jar 文件目标位置
         */
        fun getDestJar(jarInput: JarInput): File = outputProvider.getContentLocation(
            jarInput.name,
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )

        /**
         * 获取 DirInput 目标目录
         */
        fun getDestDir(directoryInput: DirectoryInput): File = outputProvider.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes,
            directoryInput.scopes,
            Format.DIRECTORY
        )


        /**
         * 获取 DirInput 子文件的目的文件
         */
        fun DirectoryInput.getDestFile(inputFile: File): File = File(getDestDir(this), inputFile.relativeTo(this.file).path)


        fun isIncrementalBuild(): Boolean = this.isIncremental

        val wholeInputs = this.inputs.asSequence()

        return if (isIncrementalBuild()) {

            val jarsEntries: List<TransformEntry> = wholeInputs.flatMap { it.jarInputs }
                .filterNot { it.status == GradleTransformStatus.NOTCHANGED }
                .map { jarInput: JarInput ->
                    when (jarInput.status) {
                        GradleTransformStatus.ADDED, GradleTransformStatus.CHANGED -> JarTransformEntry(jarInput.file, getDestJar(jarInput))
                        GradleTransformStatus.REMOVED -> DeleteTransformEntry(jarInput.file, getDestJar(jarInput))
                        else -> throw IllegalStateException("Illegal status.")
                    }
                }.toList()


            val dirEntries: List<TransformEntry> = wholeInputs.flatMap { it.directoryInputs  }
                .map { dirInput: DirectoryInput ->

                    dirInput.changedFiles.entries
                        .filterNot { entry -> entry.value == GradleTransformStatus.NOTCHANGED }
                        .map { (file, status) ->
                            when (status) {
                                GradleTransformStatus.ADDED, GradleTransformStatus.CHANGED -> DirTransformEntry(file, dirInput.getDestFile(file))
                                GradleTransformStatus.REMOVED -> DeleteTransformEntry(file, dirInput.getDestFile(file))
                                else -> throw  IllegalStateException("Illegal status.")
                            }
                        }

                }.flatten().toList()

            jarsEntries + dirEntries

        } else {

            val jarsEntries: List<TransformEntry> = wholeInputs
                .flatMap { it.jarInputs }
                .map { jar: JarInput -> jar.file to getDestJar(jar) }
                .map { JarTransformEntry(it.first, it.second) }
                .toList()

            val dirEntries: List<TransformEntry> = wholeInputs
                .flatMap { it.directoryInputs }
                .map { dir: DirectoryInput -> dir.file to getDestDir(dir) }
                .map { DirTransformEntry(it.first, it.second) }
                .toList()

            jarsEntries + dirEntries
        }
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
