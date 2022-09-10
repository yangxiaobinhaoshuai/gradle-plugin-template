package me.yangxiaobin.lib

import com.android.build.api.transform.TransformInvocation
import me.yangxiaobin.lib.ext.neatName
import me.yangxiaobin.lib.ext.requireAppExtension
import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.transform_v3.AbsGradleTransform
import me.yangxiaobin.lib.transform_v3.CopyTransform
import org.gradle.api.Project

const val ESC = '\u001B'

const val CSI_RESET = "$ESC[0m"

const val CSI_RED = "$ESC[31m"
fun red(s: Any) = "${CSI_RED}${s}${CSI_RESET}"

class TestBasePlugin : BasePlugin() {

    override val LOG_TAG: String get() = this.neatName

    override val myLogger: ILog get() = super.myLogger.setGlobalPrefix("TB @ ")

    override fun apply(p: Project) {
        super.apply(p)
        logI("${red("✓")} apply TestBasePlugin")

        val transform = TestBaseTransform(this)

        afterEvaluate {
            this.requireAppExtension.registerTransform(transform)
        }
    }
}

class TestBaseTransform(d: LogAware) : CopyTransform(d) {

    override fun transform(transformInvocation: TransformInvocation) {
        logI("${this.neatName} start ====>")
        super.transform(transformInvocation)
    }
}
