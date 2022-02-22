package me.yangxiaobin.kotlin.codelab.log


typealias LogFun = (message: String) -> Unit

fun ILog.logI(tag: String): LogFun = fun(message: String) = this.i(tag, message)

/**
 * Usage : val logD = ILog.logD(TAG)
 *         logD("actual message")
 */
fun ILog.logD(tag: String): LogFun = fun(message: String) = this.d(tag, message)

fun ILog.logE(tag: String): LogFun = fun(message: String) = this.e(tag, message)

abstract class AbsLogger : ILog {

    protected open var currentLevel: LogLevel = LogLevel.DEBUG
    protected open val innerConfig by lazy { SystemOutLogger.getConfig() }

    override fun v(tag: String, message: String) {
        logPriority(LogLevel.VERBOSE, tag, message)
    }

    override fun i(tag: String, message: String) {
        logPriority(LogLevel.INFO, tag, message)
    }

    override fun d(tag: String, message: String) {
        logPriority(LogLevel.DEBUG, tag, message)
    }

    override fun e(tag: String, message: String) {
        logPriority(LogLevel.ERROR, tag, message)
    }

    protected open fun logPriority(level: LogLevel, tag: String, message: String) {

        if (!innerConfig.enable && currentLevel < level) return

        printLog(innerConfig.printer,level,tag,message)
    }

    abstract fun printLog(printer: IPrinter?, level: LogLevel, tag: String, message: String)

}

object SystemOutLogger : AbsLogger() {

    override fun getConfig(): ILogConfig {
        return DefaultLogConfig()
    }

    override fun printLog(printer: IPrinter?, level: LogLevel, tag: String, message: String) {
        printer?.printMessage("$tag : $message")
    }

}
