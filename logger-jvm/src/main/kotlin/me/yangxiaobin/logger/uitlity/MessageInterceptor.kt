package com.zyb.iot.pad.ack.base.log

import me.yangxiaobin.logger.core.LogLevel
import me.yangxiaobin.logger.domain.DomainElement
import me.yangxiaobin.logger.elements.LogPrinterDelegate
import me.yangxiaobin.logger.elements.LogPrinterLogElement
import me.yangxiaobin.logger.uitlity.DomainElementInterceptor
import me.yangxiaobin.logger.uitlity.LogPrinter


class MessageReplaceDelegate(
    val replacement: (oldMessage: String) -> String, printer: LogPrinter
) : LogPrinterDelegate(printer) {
    override fun print(
        level: LogLevel, tag: String, message: String, throwable: Throwable?
    ) {
        this.printer.print(level, tag, replacement(message), throwable)
    }
}

class MessagePrefixInterceptor(private val messagePrefix: String) : DomainElementInterceptor {

    override fun wantIntercept(element: DomainElement?): Boolean = element is LogPrinterLogElement

    override fun transform(element: DomainElement?): DomainElement? {
        if (element is LogPrinterLogElement) {
            val originalPrinter = element.logPrinter
            val delegate = MessageReplaceDelegate(
                replacement = { oldMessage -> "$messagePrefix$oldMessage" },
                printer = originalPrinter
            )
            return LogPrinterLogElement(delegate)
        }
        return element
    }

}

class MessageSuffixInterceptor(private val messageSuffix: String) : DomainElementInterceptor {

    override fun wantIntercept(element: DomainElement?): Boolean = element is LogPrinterLogElement

    override fun transform(element: DomainElement?): DomainElement? {
        if (element is LogPrinterLogElement) {
            val originalPrinter = element.logPrinter
            val delegate = MessageReplaceDelegate(
                replacement = { oldMessage -> "$oldMessage$messageSuffix" },
                printer = originalPrinter
            )
            return LogPrinterLogElement(delegate)
        }
        return element
    }

}