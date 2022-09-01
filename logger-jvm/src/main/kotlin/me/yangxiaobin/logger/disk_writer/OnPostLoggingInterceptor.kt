package me.yangxiaobin.logger.disk_writer

import me.yangxiaobin.logger.core.LogLevel
import me.yangxiaobin.logger.domain.DomainElement
import me.yangxiaobin.logger.elements.LogPrinterDelegate
import me.yangxiaobin.logger.elements.LogPrinterLogElement
import me.yangxiaobin.logger.uitlity.DomainElementInterceptor
import me.yangxiaobin.logger.uitlity.LogMessageMeta
import me.yangxiaobin.logger.uitlity.LogPrinter


typealias OnLogging = (LogMessageMeta) -> Unit

fun createOnPostLoggingDelegate(p: LogPrinter, onLogging: OnLogging) = object : LogPrinterDelegate(p) {

    override fun print(level: LogLevel, tag: String, message: String) {
        p.print(level, tag, message)
        onLogging.invoke(LogMessageMeta(level, tag, message))
    }
}

class OnPostLoggingInterceptor(private val onPostLogging: OnLogging) : DomainElementInterceptor {

    override fun wantIntercept(element: DomainElement?): Boolean = element is LogPrinterLogElement

    override fun transform(element: DomainElement?): DomainElement? {
        if (element !is LogPrinterLogElement) return element

        val delegate = createOnPostLoggingDelegate(element.logPrinter, onPostLogging)

        return LogPrinterLogElement(delegate)
    }
}

