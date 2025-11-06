package com.zyb.iot.pad.ack.base.log

import com.zyb.iot.pad.ack.base.domaincontext.DomainElement
import com.zyb.iot.pad.ack.base.log.elements.DomainElementInterceptor
import com.zyb.iot.pad.ack.base.log.elements.LogPrinter
import com.zyb.iot.pad.ack.base.log.elements.LogPrinterDelegate
import com.zyb.iot.pad.ack.base.log.elements.LogPrinterLogElement
import com.zyb.iot.pad.ack.base.log.tool.LogLevel


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