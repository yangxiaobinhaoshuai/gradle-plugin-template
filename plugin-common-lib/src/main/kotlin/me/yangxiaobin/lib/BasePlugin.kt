package me.yangxiaobin.lib

import me.yangxiaobin.lib.ext.neatName
import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.InternalLogger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

@Suppress("MemberVisibilityCanBePrivate")
open class BasePlugin : Plugin<Project> {

    protected open val TAG: String = "BasePlugin"

    protected lateinit var mProject: Project
        private set

    protected lateinit var mGradle: Gradle
        private set

    protected lateinit var mLogger: GradleLogger
        private set

    protected open val myLogger: ILog get() = InternalLogger

    override fun apply(p: Project) {
        logI("${p.name} Applied ${this.neatName}.")
        mProject = p
        mGradle = p.gradle
        mLogger = p.logger
    }

    protected fun logV(message: String) = myLogger.v(TAG, message)
    protected fun logI(message: String) = myLogger.i(TAG, message)
    protected fun logD(message: String) = myLogger.d(TAG, message)
    protected fun logE(message: String) = myLogger.e(TAG, message)

}
