package me.yangxiaobin.lib

import me.yangxiaobin.lib.ext.neatName
import me.yangxiaobin.lib.ext.requireAppExtension
import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.transform_v3.TransformAwareManager
import me.yangxiaobin.lib.transform_v3.TransformDispatcher
import org.gradle.api.Project

const val ESC = '\u001B'

const val CSI_RESET = "$ESC[0m"

const val CSI_RED = "$ESC[31m"
fun red(s: Any) = "${CSI_RED}${s}${CSI_RESET}"

class SequenceTransformPlugin : BasePlugin() {

    override val LOG_TAG: String get() = this.neatName

    override val myLogger: ILog get() = super.myLogger.setGlobalPrefix("TB @ ")

    override fun apply(p: Project) {
        super.apply(p)
        logI("${red("✓")} apply TestBasePlugin")

        //val legacyTransform = AbsLegacyTransform(p)
        val dispatchTransformer = TransformDispatcher(this)

        afterEvaluate {
            TransformAwareManager.registerTransformAware(dispatchTransformer)
            this.requireAppExtension.registerTransform(dispatchTransformer)
        }
    }
}

