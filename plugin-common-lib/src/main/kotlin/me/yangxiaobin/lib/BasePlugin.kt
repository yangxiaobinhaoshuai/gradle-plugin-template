package me.yangxiaobin.lib

import me.yangxiaobin.lib.ext.neatName
import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.log.LogAwareImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

private const val LOG_TAG = "BasePlugin"

@Suppress("MemberVisibilityCanBePrivate")
open class BasePlugin : Plugin<Project>, LogAware by LogAwareImpl(InternalLogger, LOG_TAG) {

    protected lateinit var mProject: Project
        private set

    protected lateinit var mGradle: Gradle
        private set


    override fun apply(p: Project) {
        logI("${p.name} Applied ${this.neatName}.")
        mProject = p
        mGradle = p.gradle
    }

}
