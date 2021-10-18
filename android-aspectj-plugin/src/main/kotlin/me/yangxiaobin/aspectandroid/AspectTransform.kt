package me.yangxiaobin.aspectandroid

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.internal.pipeline.TransformManager
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.transform.AbsLegacyTransform
import java.io.File
import java.util.function.Function


class AspectTransform : AbsLegacyTransform() {


    init {
        logger.setLevel(LogLevel.VERBOSE)
    }

    override fun getName(): String = "AspectTransform"

    override fun getScopes(): MutableSet<QualifiedContent.ScopeType> = TransformManager.SCOPE_FULL_PROJECT

    override fun getJarTransformer(): Function<ByteArray, ByteArray>? {
        return getClassTransformer()
    }

    override fun isClassValid(f: File): Boolean {
        return super.isClassValid(f)
    }

    override fun isJarValid(jar: File): Boolean {
        return super.isJarValid(jar)
    }
}
