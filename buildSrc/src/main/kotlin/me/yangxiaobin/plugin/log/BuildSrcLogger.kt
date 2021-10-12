package me.yangxiaobin.plugin.log

import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.ILogImpl
import me.yangxiaobin.lib.log.LogLevel


object BuildSrcLogger : ILog by (ILogImpl().apply {
    this.setGlobalPrefix("BuildSrc/").setLevel(LogLevel.INFO)
})
