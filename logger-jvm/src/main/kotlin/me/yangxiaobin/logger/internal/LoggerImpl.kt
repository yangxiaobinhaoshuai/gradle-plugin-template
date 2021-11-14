package me.yangxiaobin.logger.internal

import me.yangxiaobin.logger.core.AbsLogger
import me.yangxiaobin.logger.domain.DomainContext

internal class LoggerImpl(private val newLogContext: DomainContext? = null) : AbsLogger() {

    private val combinedContext by lazy {
        if (newLogContext != null) super.logContext + newLogContext
        else super.logContext
    }

    override val logContext: DomainContext get() = combinedContext
}
