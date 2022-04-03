package me.yangxiaobin.logger

import me.yangxiaobin.logger.RawLogger.clone
import me.yangxiaobin.logger.core.LogFacade
import me.yangxiaobin.logger.core.LogLevel
import me.yangxiaobin.logger.domain.DomainContext
import me.yangxiaobin.logger.domain.EmptyDomainContext
import me.yangxiaobin.logger.elements.*
import me.yangxiaobin.logger.internal.LoggerImpl
import me.yangxiaobin.logger.uitlity.LogPrinter

/**
 * Used for [clone]
 */
object RawLogger : LogFacade by LoggerImpl()

/**
 * In most cases, you should create you own logger instance by calling this for your own specific configs.
 *
 * Preserve the previous context.
 */
fun LogFacade.clone(
    enable: Boolean? = null,
    globalTagPrefix: String? = null,
    globalTagSuffix: String? = null,
    logLevel: LogLevel? = null,
    printer: LogPrinter? = null,
    newLogContext: DomainContext? = null
): LogFacade {

    var mergedContext: DomainContext = EmptyDomainContext

    if (enable != null) mergedContext += EnableLogElement(enable)
    if (logLevel != null) mergedContext += LogLevelLogElement(logLevel)
    if (globalTagPrefix != null) mergedContext += GlobalTagPrefixLogElement(globalTagPrefix)
    if (globalTagSuffix != null) mergedContext += GlobalTagSuffixLogElement(globalTagSuffix)
    if (printer != null) mergedContext += LogPrinterLogElement(printer)
    if (newLogContext != null) mergedContext += newLogContext

    return this.clone(mergedContext)
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


