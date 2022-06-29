package me.yangxiaobin.test

import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.ILogImpl
import me.yangxiaobin.lib.log.LogLevel

object TestLogger : ILog by (ILogImpl().apply {
    this.setGlobalPrefix("Test/")
        .setLevel(LogLevel.VERBOSE)
})
