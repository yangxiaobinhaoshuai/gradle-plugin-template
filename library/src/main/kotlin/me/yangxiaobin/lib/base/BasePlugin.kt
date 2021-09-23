package me.yangxiaobin.lib.base

import me.yangxiaobin.lib.log.Level
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.gradle.api.Plugin
import org.gradle.api.Project

class BasePlugin : Plugin<Project> {

    val logI = Logger.log(Level.INFO, "BaseProjectPlugin")

    override fun apply(target: Project) {
        logI("${target.name} Applied baseProjectPlugin")
    }

}
