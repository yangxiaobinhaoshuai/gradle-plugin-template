package me.yangxiaobin.logger.internal

import me.yangxiaobin.logger.core.LogLevel
import me.yangxiaobin.logger.uitlity.LogPrinter

internal class SystemOutLogPrinter : LogPrinter {

    override fun print(level: LogLevel, tag: String, message: String) = println("$tag, $message")

}
