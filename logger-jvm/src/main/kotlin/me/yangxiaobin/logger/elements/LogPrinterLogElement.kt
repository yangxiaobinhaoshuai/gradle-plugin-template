package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.domain.AbsDomainElement
import me.yangxiaobin.logger.domain.AbsKey
import me.yangxiaobin.logger.uitlity.LogPrinter

open class LogPrinterDelegate(private val printer: LogPrinter) : LogPrinter by printer

data class LogPrinterLogElement(val logPrinter: LogPrinter) : AbsDomainElement(LogPrinterLogElement) {

    companion object Key : AbsKey<LogPrinterLogElement>()

}
