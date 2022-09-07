package com.test_a

import com.android.build.api.transform.TransformInvocation
import me.yangxiaobin.lib.BasePlugin
import me.yangxiaobin.lib.ext.requireAppExtension
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.transform.AbsTransformV2
import org.gradle.api.Project

class TestATransform(logDelegate: LogAware) : AbsTransformV2(logDelegate) {

    override fun transform(transformInvocation: TransformInvocation) {
        logI("TestATransform isIncremental: $isIncremental.")
        super.transform(transformInvocation)
    }

}

class TestAPlugin : BasePlugin() {

    override fun apply(p: Project) {
        super.apply(p)
        logI("Applied Test A Plugin.")

        afterEvaluate {
            this.requireAppExtension.registerTransform(TestATransform(this@TestAPlugin))
        }

    }
}
