package me.yangxiaobin.base_lib.base

import me.yangxiaobin.base_lib.Level
import me.yangxiaobin.base_lib.Logger
import me.yangxiaobin.base_lib.log
import org.gradle.api.Plugin
import org.gradle.api.Project

class BaseProjectPlugin : Plugin<Project> {

    val logI = Logger.log(Level.INFO, "BaseProjectPlugin")

    override fun apply(target: Project) {
        logI("${target.name} Applied baseProjectPlugin")
    }

}
