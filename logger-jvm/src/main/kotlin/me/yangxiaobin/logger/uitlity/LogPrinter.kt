package me.yangxiaobin.logger.uitlity

import me.yangxiaobin.logger.core.LogLevel

data class LogMessageMeta(val level: LogLevel, val tag: String, val message: String)

interface LogPrinter {

    fun print(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    )
}
