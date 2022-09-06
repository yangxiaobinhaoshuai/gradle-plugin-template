package me.yangxiaobin.lib.log

import me.yangxiaobin.lib.ext.currentProcessId
import me.yangxiaobin.lib.ext.currentWorkPath
import me.yangxiaobin.logger.RawLogger
import me.yangxiaobin.logger.core.LogFacade
import me.yangxiaobin.logger.disk_writer.DiskWriterManager
import me.yangxiaobin.logger.disk_writer.OnPostLoggingInterceptor
import me.yangxiaobin.logger.domain.DomainContext
import me.yangxiaobin.logger.domain.EmptyDomainContext
import me.yangxiaobin.logger.elements.*
import java.text.SimpleDateFormat
import java.util.*


typealias ExternalLogger = RawLogger
typealias ExternalLogLevel = me.yangxiaobin.logger.core.LogLevel
typealias ExternalLogPrinter = me.yangxiaobin.logger.uitlity.LogPrinter


class ExternalLogAdapter : AbsLogger() {

    private val defaultExternalLogger: LogFacade by lazy { ExternalLogger }
    private var externalLogger = defaultExternalLogger

    private var newLogContext: DomainContext = DiskWritingContext

    private val actualLogger by lazy { externalLogger.clone(newLogContext) }

    override fun isEnable(enable: Boolean): AbsLogger {
        newLogContext += EnableLogElement(enable)
        return super.isEnable(enable)
    }

    override fun setLevel(level: LogLevel): AbsLogger {
        newLogContext += LogLevelLogElement(ExternalLogLevel.valueOf(level.toString()))
        return super.setLevel(level)
    }

    override fun setGlobalPrefix(prefix: String): AbsLogger {
        newLogContext += GlobalTagPrefixLogElement(prefix)
        return super.setGlobalPrefix(prefix)
    }

    override fun setGlobalSuffix(suffix: String): AbsLogger {
        newLogContext += GlobalTagSuffixLogElement(suffix)
        return super.setGlobalSuffix(suffix)
    }

    override fun setPrinter(printFun: (Triple<LogLevel, String, String>) -> Unit): AbsLogger {

        val printerDelegate = object : ExternalLogPrinter {
            override fun print(level: ExternalLogLevel, tag: String, message: String) {
                printFun.invoke(Triple(LogLevel.valueOf(level.toString()),tag,message))
            }
        }

        newLogContext += LogPrinterLogElement(printerDelegate)
        return super.setPrinter(printFun)
    }

    override fun v(tag: String, message: String) {
        //super.v(tag, message)
        actualLogger.v(tag,message)
    }

    override fun i(tag: String, message: String) {
        //super.i(tag, message)
        actualLogger.v(tag,message)
    }

    override fun d(tag: String, message: String) {
        //super.d(tag, message)
        actualLogger.v(tag,message)
    }

    override fun e(tag: String, message: String) {
        //super.e(tag, message)
        actualLogger.v(tag,message)
    }

    public fun setExternalLogger(external: LogFacade) = apply {
        externalLogger = external
    }

    /**
     * 实际上是对 LogFacade 的拷贝
     */
    override fun copy(): ILog {
        return ExternalLogAdapter().setExternalLogger(actualLogger)
    }

    private companion object {

        private var DiskWritingContext: DomainContext = EmptyDomainContext

        init {
            val now = SimpleDateFormat("yyyy.mm.dd.hh:mm:ss", Locale.getDefault()).format(Date())
            val logFilePath = "build/custom_log/${now}_building.log"
            // currentWorkPath = /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template
            val actualLogPath = "$currentWorkPath/$logFilePath"

            DiskWriterManager.setConfig {
                this.logFileName = actualLogPath
                this.pid = currentProcessId
                this.processName = "process:$currentProcessId"
            }

            val preInterceptor = OnPostLoggingInterceptor(DiskWriterManager::addLog)
            DiskWritingContext = InterceptorLogElement(preInterceptor)

            /**
             * Start recording log when this was initiated.
             */
            DiskWriterManager.startSession()
        }
    }
}
