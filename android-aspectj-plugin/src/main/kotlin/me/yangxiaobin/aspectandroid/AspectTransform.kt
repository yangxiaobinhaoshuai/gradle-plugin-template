package me.yangxiaobin.aspectandroid

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.internal.pipeline.TransformManager
import me.yangxiaobin.lib.transform.AbsLegacyTransform
import me.yangxiaobin.lib.transform.ByteCodeTransformer
import me.yangxiaobin.lib.transform.DefaultByteCodeTransformer


class AspectTransform : AbsLegacyTransform() {

    override fun getName(): String = "AspectTransform"

    override fun getScopes(): MutableSet<QualifiedContent.ScopeType> = TransformManager.SCOPE_FULL_PROJECT

    override fun getJarTransformer(): ByteCodeTransformer {
        return DefaultByteCodeTransformer()
    }
}
