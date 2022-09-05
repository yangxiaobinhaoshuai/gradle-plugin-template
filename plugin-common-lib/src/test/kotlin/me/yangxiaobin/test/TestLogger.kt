package me.yangxiaobin.test

import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.ExternalLogAdapter
import me.yangxiaobin.lib.log.LogLevel

object TestLogger : ILog by (ExternalLogAdapter().apply {
    this.setGlobalPrefix("Test/")
        .setLevel(LogLevel.VERBOSE)
})
