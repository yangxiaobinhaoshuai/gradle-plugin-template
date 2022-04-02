package me.yangxiaobin.logger

import me.yangxiaobin.logger.core.LogAction
import me.yangxiaobin.logger.core.LogFacade
import me.yangxiaobin.logger.core.LogLevel
import me.yangxiaobin.logger.domain.DomainContext
import me.yangxiaobin.logger.domain.EmptyDomainContext
import me.yangxiaobin.logger.elements.EnableLogElement
import me.yangxiaobin.logger.elements.GlobalTagPrefixLogElement
import me.yangxiaobin.logger.elements.GlobalTagSuffixLogElement
import me.yangxiaobin.logger.elements.LogLevelLogElement
import me.yangxiaobin.logger.internal.LoggerImpl

/**
 * Used for [clone]
 */
internal val defaultLoggerImpl = LoggerImpl()
object RawLogger : LogFacade by defaultLoggerImpl

/**
 * In most cases, you should create you own logger instance by calling this for your own specific configs.
 */
fun LogFacade.clone(newLogContext: DomainContext? = null): LogFacade = LoggerImpl(newLogContext)

/**
 * View current logger config.
 */
fun LogFacade.dumpDomainContext(): String {
    val logAction = if (this is RawLogger) defaultLoggerImpl else this
    logAction as LogAction
    return logAction.logContext.dump()
}


fun LogFacade.clone(
    enable: Boolean? = null,
    globalTagPrefix: String? = null,
    globalTagSuffix: String? = null,
    logLevel: LogLevel? = null,
    newLogContext: DomainContext? = null
): LogFacade {

    var mergedContext: DomainContext = EmptyDomainContext

    if (enable != null) mergedContext += EnableLogElement(enable)
    if (logLevel != null) mergedContext += LogLevelLogElement(logLevel)
    if (globalTagPrefix != null) mergedContext += GlobalTagPrefixLogElement(globalTagPrefix)
    if (globalTagSuffix != null) mergedContext += GlobalTagSuffixLogElement(globalTagSuffix)
    if (newLogContext != null) mergedContext += newLogContext

    return LoggerImpl(mergedContext)
}

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


