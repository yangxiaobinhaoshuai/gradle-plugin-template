package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.AbsKey
import me.yangxiaobin.logger.AbsLogElement
import me.yangxiaobin.logger.LogLevel

data class LogLevelLogElement(val level: LogLevel) : AbsLogElement(LogLevelLogElement) {

    companion object Key : AbsKey<LogLevelLogElement>()

}
