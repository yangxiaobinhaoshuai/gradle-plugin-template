package me.yangxiaobin.lib

import me.yangxiaobin.lib.ext.neatName
import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.log.LogDelegate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

private const val LOG_TAG = "BasePlugin"

@Suppress("MemberVisibilityCanBePrivate")
open class BasePlugin : Plugin<Project>, LogAware by LogDelegate(InternalLogger, LOG_TAG) {

    protected lateinit var mProject: Project
        private set

    protected lateinit var mGradle: Gradle
        private set


    override fun apply(p: Project) {
        logI("${p.name} Applied ${this.neatName}.")
        mProject = p
        mGradle = p.gradle
    }

    protected fun requireProject(): Project = mProject

    protected fun afterEvaluate(block: Project.() -> Unit) {
        requireProject().afterEvaluate(block)
    }

    override fun logV(message: String) {
        super.logV(message)
    }

    override fun logI(message: String) {
        super.logI(message)
    }

    override fun logD(message: String) {
        super.logD(message)
    }

    override fun logE(message: String) {
        super.logE(message)
    }
}
