package me.yangxiaobin.aspectandroid

import me.yangxiaobin.lib.transform.DefaultByteCodeTransformer
import me.yangxiaobin.lib.transform.AbsLegacyTransform
import me.yangxiaobin.lib.transform.ByteCodeTransformer


class AspectTransform : AbsLegacyTransform() {

    override fun getName(): String = "AspectTransform"


    override fun getJarTransformer(): ByteCodeTransformer? {
        return DefaultByteCodeTransformer()
    }
}
