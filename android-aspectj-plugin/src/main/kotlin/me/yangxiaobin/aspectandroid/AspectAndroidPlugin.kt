package me.yangxiaobin.aspectandroid

import me.yangxiaobin.lib.base.BasePlugin
import org.gradle.api.Project

class AspectAndroidPlugin : BasePlugin() {

    override val TAG: String get() = "AAP"

    override fun apply(p: Project) {
        super.apply(p)

        logI("${p.name} applied AspectAndroidPlugin.")

    }
}
