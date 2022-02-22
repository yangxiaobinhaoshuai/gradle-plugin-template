package me.yangxiaobin.sample.log

import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.ILogImpl
import me.yangxiaobin.lib.log.LogLevel


object AppLogger : ILog by (ILogImpl().apply {
    this.setGlobalPrefix("App/")
        .setLevel(LogLevel.VERBOSE)
})
