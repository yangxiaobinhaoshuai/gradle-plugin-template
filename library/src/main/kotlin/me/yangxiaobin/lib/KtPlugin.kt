package me.yangxiaobin.lib

import me.yangxiaobin.lib.base.BasePlugin
import me.yangxiaobin.lib.ext.getAppExtension
import me.yangxiaobin.lib.transform.AbsLegacyTransform
import org.gradle.api.Project

class KtPlugin : BasePlugin() {

    override val TAG: String
        get() = "KtPlugin"

    override fun apply(p: Project) {
        super.apply(p)
        logI("${p.name} ==> Applied KtPlugin.")

        p.afterEvaluate {
            p.getAppExtension?.registerTransform(AbsLegacyTransform())
        }

    }

}
