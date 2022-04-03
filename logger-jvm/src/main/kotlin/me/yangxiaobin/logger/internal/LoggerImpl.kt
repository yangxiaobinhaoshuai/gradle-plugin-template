package me.yangxiaobin.logger.internal

import me.yangxiaobin.logger.core.AbsLogger
import me.yangxiaobin.logger.core.LogFacade
import me.yangxiaobin.logger.domain.DomainContext
import me.yangxiaobin.logger.domain.EmptyDomainContext

internal class LoggerImpl(private val newLogContext: DomainContext? = null) : AbsLogger() {

    private val combinedContext by lazy {
        if (newLogContext != null) super.logContext + newLogContext
        else super.logContext
    }

    override val logContext: DomainContext get() = combinedContext

    override fun clone(newLogContext: DomainContext?): LogFacade {
        val newContext = logContext + (newLogContext ?: EmptyDomainContext)
        return LoggerImpl(newContext)
    }

    override fun dumpContext(): String = logContext.dump()
}
