package me.yangxiaobin.lib.transform_v3

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.variant.VariantInfo
import me.yangxiaobin.lib.GradleTransform
import me.yangxiaobin.lib.ext.neatName
import me.yangxiaobin.lib.log.LogAware

open class AbsGradleTransform(private val logDelegate: LogAware) : GradleTransform(), LogAware by logDelegate {

    override val LOG_TAG: String get() = "AbsGradleTransformV3"

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

}
