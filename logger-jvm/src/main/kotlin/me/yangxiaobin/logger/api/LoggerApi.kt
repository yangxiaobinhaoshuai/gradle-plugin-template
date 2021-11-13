package me.yangxiaobin.logger.api

import me.yangxiaobin.logger.LogAction
import me.yangxiaobin.logger.LogContext
import me.yangxiaobin.logger.LogLevel
import me.yangxiaobin.logger.internal.AbsLogger


internal class LoggerImpl(private val newLogContext: LogContext? = null) : AbsLogger() {

    override val logContext: LogContext
        get() = if (newLogContext != null) super.logContext + newLogContext else super.logContext
}

object Logger : LogAction by LoggerImpl()

fun LogAction.clone(newLogContext: LogContext? = null): LogAction = LoggerImpl(newLogContext)

/**
 * Function currying.
 */
fun LogAction.log(level: LogLevel, tag: String) = fun(message: String) =
    when (level) {
        LogLevel.VERBOSE -> this.v(tag, message)
        LogLevel.INFO -> this.i(tag, message)
        LogLevel.DEBUG -> this.d(tag, message)
        LogLevel.ERROR -> this.e(tag, message)
    }


