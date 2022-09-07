package com.test

import com.android.build.api.transform.TransformInvocation
import me.yangxiaobin.lib.BasePlugin
import me.yangxiaobin.lib.ext.neatName
import me.yangxiaobin.lib.ext.requireAppExtension
import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.transform.AbsLegacyTransform
import me.yangxiaobin.lib.transform.AbsTransformV2
import org.gradle.api.Project
import java.util.function.Function

class TestLegacyTransform(p: Project) : AbsLegacyTransform(p) {

    override fun isIncremental(): Boolean = false

    override fun getJarTransformer(): Function<ByteArray, ByteArray>? {
        return super.getJarTransformer()
    }

    override fun getClassTransformer(): Function<ByteArray, ByteArray>? {
        return super.getClassTransformer()
    }

}

class TestTransformV2(logDelegate: LogAware) : AbsTransformV2(logDelegate) {

    override val LOG_TAG: String get() = "TestTransformV2"

    override fun isIncremental() = true

    override fun transform(transformInvocation: TransformInvocation) {
        logI("TestTransformV2 isIncremental: $isIncremental.")
        super.transform(transformInvocation)
    }
}


/**
 * force execution transform.
 *
 *   v2 transform cmd
 *
 *   :logger-jvm:publishToMavenLocal  :plugin-common-lib:publishToMavenLocal :samples:androidapp:transformClassesWithTestTransformV2ForDebug --rerun-tasks -s
 *
 *    :samples:androidapp:transformClassesWithTestTransformV2ForDebug --rerun-tasks -s
 *
 *   legacy tranform cmdb
 *    :samples:androidapp:transformClassesWithAbsLegacyTransformForDebug --rerun-tasks -s
 */
class TestPlugin : BasePlugin() {

    override val LOG_TAG: String get() = this.neatName

    override val myLogger: ILog get() = super.myLogger.setGlobalPrefix("|T|")


    override fun apply(p: Project) {
        super.apply(p)

        afterEvaluate {
            //p.requireAppExtension.registerTransform(TestLegacyTransform(p))
            logI("registered transform")
            p.requireAppExtension.registerTransform(TestTransformV2(this@TestPlugin))
        }
    }
}
