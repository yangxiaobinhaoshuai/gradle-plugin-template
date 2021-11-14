package me.yangxiaobin.logger.core

import me.yangxiaobin.logger.domain.DomainContext
import me.yangxiaobin.logger.internal.LogActionImpl

abstract class AbsLogger : LogFacade, LogAction by LogActionImpl() {

    private val logWithContext by lazy { logCurryingWithContext(logContext) }

    private fun logCurryingWithContext(logContext: DomainContext) =
        fun(level: LogLevel, tag: String, message: String) = logPriority(logContext, level, tag, message)

    override fun v(tag: String, message: String) = logWithContext(LogLevel.VERBOSE, tag, message)

    override fun i(tag: String, message: String) = logWithContext(LogLevel.INFO, tag, message)

    override fun d(tag: String, message: String) = logWithContext(LogLevel.DEBUG, tag, message)

    override fun e(tag: String, message: String) = logWithContext(LogLevel.ERROR, tag, message)

}

