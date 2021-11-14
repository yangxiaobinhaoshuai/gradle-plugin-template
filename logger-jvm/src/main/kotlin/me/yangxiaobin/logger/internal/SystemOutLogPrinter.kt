package me.yangxiaobin.logger.internal

import me.yangxiaobin.logger.uitlity.LogPrinter

internal class SystemOutLogPrinter : LogPrinter {

    override fun print(tag: String, message: String) = println("$tag, $message")

}
