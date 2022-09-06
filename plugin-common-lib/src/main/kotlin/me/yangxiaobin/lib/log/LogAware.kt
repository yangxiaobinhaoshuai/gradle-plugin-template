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

data class LogAwareImpl(override val myLogger: ILog, override val LOG_TAG: String) : LogAware
