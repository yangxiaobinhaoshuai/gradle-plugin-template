package me.yangxiaobin.lib

import com.android.build.gradle.AppPlugin
import me.yangxiaobin.lib.base.BasePlugin
import org.gradle.api.Project

class KtPlugin : BasePlugin() {

    override val TAG: String
        get() = "KtPlugin"

    override fun apply(p: Project) {
        super.apply(p)
        logI("${p.name} ==> Applied KtPlugin.")
    }

}
