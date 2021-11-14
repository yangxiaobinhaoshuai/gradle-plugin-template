package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.domain.AbsKey
import me.yangxiaobin.logger.domain.AbsDomainElement
import me.yangxiaobin.logger.core.LogLevel

data class LogLevelLogElement(val level: LogLevel) : AbsDomainElement(LogLevelLogElement) {

    companion object Key : AbsKey<LogLevelLogElement>()

}
