package me.yangxiaobin.lib.base

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BasePlugin : Plugin<Project> {

    open val TAG: String = "BasePlugin"

    @Suppress("LeakingThis")
    val logI = Logger.log(LogLevel.INFO, TAG)

    override fun apply(p: Project) {
        logI("${p.name} Applied basePlugin")
    }

}
