package me.yangxiaobin.logger.uitlity

import me.yangxiaobin.logger.core.LogLevel

interface LogPrinter {

    fun print(level: LogLevel, tag: String, message: String)
}
