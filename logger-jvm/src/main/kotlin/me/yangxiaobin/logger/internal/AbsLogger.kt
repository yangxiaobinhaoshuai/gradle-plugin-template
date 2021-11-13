package me.yangxiaobin.logger.internal

import me.yangxiaobin.logger.LogAction
import me.yangxiaobin.logger.LogContext
import me.yangxiaobin.logger.LogLevel
import me.yangxiaobin.logger.elements.*


internal abstract class AbsLogger : LogAction {

    override val logContext: LogContext
        get() = setOf<LogContext>(
            EnableLogElement(true),
            LogLevelLogElement(LogLevel.INFO),
            TagPrefixLogElement("Prefix"),
            TagSuffixLogElement("Suffix"),
            LogPrinterLogElement(SystemOutLogPrinter()),
        ).reduce { acc, e -> acc + e }

    override fun v(tag: String, message: String) = logPriority(LogLevel.VERBOSE, tag, message)

    override fun i(tag: String, message: String) = logPriority(LogLevel.INFO, tag, message)

    override fun d(tag: String, message: String) = logPriority(LogLevel.DEBUG, tag, message)

    override fun e(tag: String, message: String) = logPriority(LogLevel.ERROR, tag, message)

    private fun logPriority(level: LogLevel, tag: String, message: String) {
        val curLevel: LogLevel? = logContext[LogLevelLogElement]?.level

        val formatter: FormatLogElement? = logContext[FormatLogElement]

        val tagPrefix: String = logContext[TagPrefixLogElement]?.tagPrefix ?: ""
        val tagSuffix: String = logContext[TagSuffixLogElement]?.tagSuffix ?: ""

        val actualTag = tagPrefix + tag + tagSuffix

        if (curLevel == null || curLevel <= level) {

            val (formatTag, formatMessage) = formatter?.formatter?.format(actualTag to message)
                ?: (actualTag to message)

            logContext[LogPrinterLogElement]?.logPrinter?.print(formatTag, formatMessage)
        }
    }

}

