package com.test

import me.yangxiaobin.lib.BasePlugin
import me.yangxiaobin.lib.ext.getAppExtension
import me.yangxiaobin.lib.ext.neatName
import me.yangxiaobin.lib.log.ILog
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

class TestTransformV2(l:ILog) : AbsTransformV2(l) {

}


class TestPlugin : BasePlugin() {

    override val TAG: String get() = this.neatName

    override val myLogger: ILog get() = super.myLogger.setGlobalPrefix("|T|")

    override fun apply(p: Project) {
        super.apply(p)


        for(i in 1..10){
            logD("I'm logger -----> $i.")
        }

        p.afterEvaluate {
            //p.getAppExtension?.registerTransform(TestLegacyTransform(p))
            p.getAppExtension?.registerTransform(TestTransformV2(myLogger))
        }
    }
}
