package me.yangxiaobin.aspectandroid

import me.yangxiaobin.lib.base.BasePlugin
import me.yangxiaobin.lib.ext.getAppExtension
import org.gradle.api.Project

class AspectAndroidPlugin : BasePlugin() {

    override val TAG: String get() = "AAP"

    override fun apply(p: Project) {
        super.apply(p)

        logI("${p.name} applied AspectAndroidPlugin.")

        p.afterEvaluate { p.getAppExtension?.registerTransform(AspectTransform()) }

    }
}
