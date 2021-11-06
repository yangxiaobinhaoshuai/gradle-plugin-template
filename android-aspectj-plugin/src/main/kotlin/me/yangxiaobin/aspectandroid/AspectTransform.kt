package me.yangxiaobin.aspectandroid

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.internal.pipeline.TransformManager
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.transform.AbsLegacyTransform
import org.gradle.api.Project
import java.io.File
import java.util.function.Function


class AspectTransform(project: Project) : AbsLegacyTransform(project) {

    init {
        logger.setLevel(LogLevel.INFO)
    }

    override fun getName(): String = "AspectTransform"

    override fun getScopes(): MutableSet<QualifiedContent.ScopeType> = TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = true

    override fun getJarTransformer(): Function<ByteArray, ByteArray>? {
        return getClassTransformer()
    }
//
//    override fun getClassTransformer(): Function<ByteArray, ByteArray>? {
//        return super.getClassTransformer()
//    }

    override fun isClassValid(f: File): Boolean {
        return arrayOf("BuildConfig.class")
            .fold(true) { acc: Boolean, regex: String -> acc && !regex.toRegex().matches(f.name) }
    }

    override fun isJarValid(jar: File): Boolean {
        return arrayOf(
            "R.jar",
            "annotation-.+.jar",
            "jetified-annotations-.+.jar",
        ).fold(true) { acc: Boolean, regex: String -> acc && !regex.toRegex().matches(jar.name) }
    }

}
