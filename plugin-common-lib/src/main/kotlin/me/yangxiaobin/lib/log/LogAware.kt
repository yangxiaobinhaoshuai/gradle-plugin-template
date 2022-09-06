package me.yangxiaobin.lib.log

/**
 * Provide Logging function
 */
interface LogAware {

    @Suppress("PropertyName")
    val LOG_TAG: String

    val myLogger: ILog

    fun logV(message: String) = myLogger.v(LOG_TAG, message)
    fun logI(message: String) = myLogger.i(LOG_TAG, message)
    fun logD(message: String) = myLogger.d(LOG_TAG, message)
    fun logE(message: String) = myLogger.e(LOG_TAG, message)
}

/**
 * 由于 by delegate 的实现原理原因，子类如想通过重写 myLogger 和 LOG_TAG 两个属性，需要覆盖所有 logV/I/D/E 方法来让属性生效
 */
data class LogDelegate(override val myLogger: ILog, override val LOG_TAG: String) : LogAware
