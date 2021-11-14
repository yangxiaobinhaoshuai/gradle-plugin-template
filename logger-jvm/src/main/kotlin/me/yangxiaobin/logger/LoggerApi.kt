package me.yangxiaobin.logger

import me.yangxiaobin.logger.core.LogFacade
import me.yangxiaobin.logger.core.LogLevel
import me.yangxiaobin.logger.domain.DomainContext
import me.yangxiaobin.logger.internal.LoggerImpl

/**
 * Used for [clone]
 */
object RawLogger : LogFacade by LoggerImpl()

/**
 * In most cases, you should create you own logger instance by calling this for your own specific configs.
 */
fun LogFacade.clone(newLogContext: DomainContext? = null): LogFacade = LoggerImpl(newLogContext)


/**
 * Function currying.
 * e.g.
 *    val logI = LogFacade.clone().log(LogLevel.INFO,TAG)
 *    logI( your log message)
 */
fun LogFacade.log(level: LogLevel, tag: String) = fun(message: String) =
    when (level) {
        LogLevel.VERBOSE -> this.v(tag, message)
        LogLevel.INFO -> this.i(tag, message)
        LogLevel.DEBUG -> this.d(tag, message)
        LogLevel.ERROR -> this.e(tag, message)
    }


