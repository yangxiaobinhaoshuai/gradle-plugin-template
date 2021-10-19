package me.yangxiaobin.lib.base

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("MemberVisibilityCanBePrivate")
abstract class BasePlugin : Plugin<Project> {

    protected open val TAG: String = "BasePlugin"

    protected lateinit var mProject: Project
        private set

    protected lateinit var mLogger: org.gradle.api.logging.Logger
        private set

    protected val logV by lazy { Logger.log(LogLevel.VERBOSE, TAG) }
    protected val logI by lazy { Logger.log(LogLevel.INFO, TAG) }
    protected val logD by lazy { Logger.log(LogLevel.DEBUG, TAG) }
    protected val logE by lazy { Logger.log(LogLevel.ERROR, TAG) }

    override fun apply(p: Project) {
        logI("${p.name} Applied basePlugin")
        mProject = p
        mLogger = p.logger
    }

}
