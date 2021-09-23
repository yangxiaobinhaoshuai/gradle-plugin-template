package me.yangxiaobin.lib

import me.yangxiaobin.lib.base.BasePlugin
import org.gradle.api.Project

class KtPlugin : BasePlugin() {

    override fun apply(target: Project) {
    }

    private companion object {
        private const val TAG = "KtPlugin"
    }

}
