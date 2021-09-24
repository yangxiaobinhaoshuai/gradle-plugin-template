package me.yangxiaobin.lib

import me.yangxiaobin.lib.base.BasePlugin
import org.gradle.api.Project

class KtPlugin : BasePlugin() {

    override val TAG: String
        get() = "KtPlugin"

    override fun apply(target: Project) {
        super.apply(target)

    }

}
