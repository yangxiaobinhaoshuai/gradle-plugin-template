package me.yangxiaobin.logger.core

import me.yangxiaobin.logger.domain.DomainContext
import me.yangxiaobin.logger.internal.LogActionImpl

abstract class AbsLogger : LogFacade, LogAction by LogActionImpl() {

    private val logWithContext by lazy { logCurriedWithContext(logContext) }

    private fun logCurriedWithContext(logContext: DomainContext) =
        fun(level: LogLevel, tag: String, message: String, throwable: Throwable?) =
            logPriority(logContext, level, tag, message, throwable)

    override fun v(tag: String, message: String) = logWithContext(LogLevel.VERBOSE, tag, message, null)

    override fun i(tag: String, message: String) = logWithContext(LogLevel.INFO, tag, message, null)

    override fun d(tag: String, message: String) = logWithContext(LogLevel.DEBUG, tag, message, null)

    override fun e(tag: String, message: String) = logWithContext(LogLevel.ERROR, tag, message, null)

    override fun e(tag: String, message: String, t: Throwable) = logWithContext(LogLevel.ERROR, tag, message, t)

}

