package me.yangxiaobin.logger.core

import me.yangxiaobin.logger.domain.DomainContext

interface LogAction {

    val logContext: DomainContext

    fun logPriority(logContext: DomainContext, level: LogLevel, tag: String, message: String)
}
