package me.yangxiaobin.logger


interface LogAction {

    val logContext: LogContext

    fun v(tag: String, message: String)

    fun i(tag: String, message: String)

    fun d(tag: String, message: String)

    fun e(tag: String, message: String)
}

enum class LogLevel { VERBOSE, INFO, DEBUG, ERROR }
