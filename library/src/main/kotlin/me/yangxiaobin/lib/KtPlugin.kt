package me.yangxiaobin.lib

import com.android.build.gradle.BaseExtension
import me.yangxiaobin.lib.base.BasePlugin
import me.yangxiaobin.lib.transform.AbsLegacyTransform
import org.gradle.api.Project

class KtPlugin : BasePlugin() {

    override val TAG: String
        get() = "KtPlugin"

    override fun apply(p: Project) {
        super.apply(p)
        logI("${p.name} ==> Applied KtPlugin.")

        p.afterEvaluate {
            (p.extensions.getByName("android") as BaseExtension).registerTransform(AbsLegacyTransform())
        }

    }

}
