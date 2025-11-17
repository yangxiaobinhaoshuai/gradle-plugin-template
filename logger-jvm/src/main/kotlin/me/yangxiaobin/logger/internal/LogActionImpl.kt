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

    override fun logPriority(
        logContext: DomainContext,
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {

        /**
         * Check whether working element has substitutes.
         */
        fun <E : DomainElement> Key<E>.check(): E? {
            val curElement: E? = logContext[this]
            val interceptElement = logContext[InterceptorLogElement]
            @Suppress("UNCHECKED_CAST")
            return (interceptElement?.interceptor?.takeIf { it.wantIntercept(curElement) }?.transform(curElement)
                ?: curElement) as? E
        }

        val enable = EnableLogElement.check()?.enable
        if (enable == false) return

        val curLevel: LogLevel? = LogLevelLogElement.check()?.level

        val formatter: FormatLogElement? = FormatLogElement.check()

        val tagPrefix: String = GlobalTagPrefixLogElement.check()?.tagPrefix ?: ""
        val tagSuffix: String = GlobalTagSuffixLogElement.check()?.tagSuffix ?: ""

        val actualTag = tagPrefix + tag + tagSuffix

        /**
         * NB. Only do logging when curLevel <= level
         */
        if (curLevel == null || curLevel <= level) {

            val (formatTag: String, formatMessage: String) = formatter
                ?.formatter
                ?.format(actualTag to message)
                ?: (actualTag to message)

            LogPrinterLogElement.check()
                ?.logPrinter
                ?.print(
                    level,
                    formatTag,
                    formatMessage,
                    throwable
                )
        }
    }

}
