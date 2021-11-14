package me.yangxiaobin.logger.internal

import me.yangxiaobin.logger.core.LogAction
import me.yangxiaobin.logger.core.LogLevel
import me.yangxiaobin.logger.domain.DomainContext
import me.yangxiaobin.logger.domain.DomainElement
import me.yangxiaobin.logger.domain.Key
import me.yangxiaobin.logger.elements.*

class LogActionImpl : LogAction {

    private val defaultElements = setOf<DomainContext>(
        EnableLogElement(true),
        LogLevelLogElement(LogLevel.VERBOSE),
        GlobalTagPrefixLogElement(""),
        GlobalTagSuffixLogElement(""),
        LogPrinterLogElement(SystemOutLogPrinter()),
    ).reduce { acc, e -> acc + e }

    override val logContext: DomainContext get() = defaultElements

    override fun logPriority(logContext: DomainContext, level: LogLevel, tag: String, message: String) {

        val interceptor: InterceptorLogElement? = logContext[InterceptorLogElement]

        fun <E : DomainElement> Key<E>.toElement(): E? {
            val element = if (interceptor?.interceptor?.intercept == true)
                interceptor.interceptor.transform(logContext[this])
            else logContext[this]
            @Suppress("UNCHECKED_CAST")
            return element as? E
        }

        val enable = EnableLogElement.toElement()?.enable
        if (enable == false) return

        val curLevel: LogLevel? = LogLevelLogElement.toElement()?.level

        val formatter: FormatLogElement? = FormatLogElement.toElement()

        val tagPrefix: String = GlobalTagPrefixLogElement.toElement()?.tagPrefix ?: ""
        val tagSuffix: String = GlobalTagSuffixLogElement.toElement()?.tagSuffix ?: ""

        val actualTag = tagPrefix + tag + tagSuffix

        if (curLevel == null || curLevel <= level) {

            val (formatTag, formatMessage) = formatter?.formatter?.format(actualTag to message)
                ?: (actualTag to message)

            LogPrinterLogElement.toElement()?.logPrinter?.print(formatTag, formatMessage)
        }
    }

}
