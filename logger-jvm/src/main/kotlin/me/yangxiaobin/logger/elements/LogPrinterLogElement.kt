package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.AbsKey
import me.yangxiaobin.logger.AbsLogElement
import me.yangxiaobin.logger.LogPrinter

data class LogPrinterLogElement(val logPrinter: LogPrinter) : AbsLogElement(LogPrinterLogElement) {

    companion object Key : AbsKey<LogPrinterLogElement>()

}
