package com.test

import me.yangxiaobin.lib.BasePlugin
import me.yangxiaobin.lib.ext.getAppExtension
import me.yangxiaobin.lib.transform.AbsLegacyTransform
import me.yangxiaobin.lib.transform.AbsTransformV2
import org.gradle.api.Project
import java.util.function.Function

class TestLegacyTransform(p: Project) : AbsLegacyTransform(p){

    override fun isIncremental(): Boolean = false

    override fun getJarTransformer(): Function<ByteArray, ByteArray>? {
        return super.getJarTransformer()
    }

    override fun getClassTransformer(): Function<ByteArray, ByteArray>? {
        return super.getClassTransformer()
    }

}

class TestTransformV2 : AbsTransformV2() {

}


class TestPlugin : BasePlugin() {

    override fun apply(p: Project) {
        super.apply(p)

        p.afterEvaluate {
//            p.getAppExtension?.registerTransform(TestLegacyTransform(p))
            p.getAppExtension?.registerTransform(TestTransformV2())
        }
    }
}
