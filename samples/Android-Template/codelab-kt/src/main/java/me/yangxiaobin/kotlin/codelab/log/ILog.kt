package me.yangxiaobin.kotlin.codelab.log


enum class LogLevel { VERBOSE, INFO, DEBUG, ERROR }

interface ILog {

    fun v(tag: String, message: String)

    fun i(tag: String, message: String)

    fun d(tag: String, message: String)

    fun e(tag: String, message: String)

    fun getConfig(): ILogConfig
}


interface ILogConfig {

    val minimumLevel: LogLevel

    val enable: Boolean

    val printer: IPrinter?
}

data class DefaultLogConfig(
    override val minimumLevel: LogLevel = LogLevel.INFO,
    override val enable: Boolean = true,
    override val printer: IPrinter? = SystemOutPrinter
) : ILogConfig

