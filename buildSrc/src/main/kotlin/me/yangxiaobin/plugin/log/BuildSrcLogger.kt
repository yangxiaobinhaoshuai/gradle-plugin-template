package me.yangxiaobin.plugin.log

import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.ILogImpl
import me.yangxiaobin.lib.log.LogLevel


object BuildSrcLogger : ILog by (ILogImpl().apply {
    this.setGlobalSuffix("BuildSrc :: ").setLevel(LogLevel.INFO)
})
