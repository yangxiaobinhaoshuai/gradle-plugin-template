package me.yangxiaobin.lib

import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

@Suppress("MemberVisibilityCanBePrivate")
abstract class BasePlugin : Plugin<Project> {

    protected open val TAG: String = "BasePlugin"

    protected lateinit var mProject: Project
        private set

    protected lateinit var mGradle: Gradle
        private set

    protected lateinit var mLogger: org.gradle.api.logging.Logger
        private set

    protected open val myLogger: ILog get() = Logger

    protected val logV by lazy { myLogger.log(LogLevel.VERBOSE, TAG) }
    protected val logI by lazy { myLogger.log(LogLevel.INFO, TAG) }
    protected val logD by lazy { myLogger.log(LogLevel.DEBUG, TAG) }
    protected val logE by lazy { myLogger.log(LogLevel.ERROR, TAG) }

    override fun apply(p: Project) {
        logI("${p.name} Applied ${this.javaClass.simpleName}.")
        mProject = p
        mGradle = p.gradle
        mLogger = p.logger
    }

}
