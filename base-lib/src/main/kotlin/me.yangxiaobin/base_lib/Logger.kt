package me.yangxiaobin.base_lib


typealias LogPrinter = (Triple<Level, String, String>) -> Unit

interface ILog {

    fun setGlobalPrefix(prefix: String): ILog

    fun setGlobalSuffix(prefix: String): ILog

    fun isEnable(enable: Boolean): ILog

    fun setLevel(level: Level): ILog

    fun setPrinter(printFun: LogPrinter): ILog

    fun v(tag: String, message: String)

    fun i(tag: String, message: String)

    fun d(tag: String, message: String)

    fun e(tag: String, message: String)

}

enum class Level { VERBOSE, INFO, DEBUG, ERROR }

abstract class AbsLogger : ILog {

    private var curLevel = Level.INFO
    private var enable: Boolean = true

    private var curPrinter: LogPrinter? = null

    private var globalPrefix: String? = null
    private var globalSuffix: String? = null


    override fun isEnable(enable: Boolean) = apply {
        this.enable = enable
    }

    override fun setLevel(level: Level) = apply {
        this.curLevel = level
    }

    override fun setGlobalPrefix(prefix: String) = apply {
        globalPrefix = prefix
    }

    override fun setGlobalSuffix(suffix: String) = apply {
        globalSuffix = suffix
    }

    override fun setPrinter(printFun: (Triple<Level, String, String>) -> Unit) = apply {
        this.curPrinter = printFun
    }

    override fun v(tag: String, message: String) = logPriority(Level.VERBOSE, tag, message)

    override fun i(tag: String, message: String) = logPriority(Level.INFO, tag, message)

    override fun d(tag: String, message: String) = logPriority(Level.DEBUG, tag, message)

    override fun e(tag: String, message: String) = logPriority(Level.ERROR, tag, message)


    private fun logPriority(level: Level, tag: String, message: String) {
        var actualTag = if (globalPrefix != null) "$globalPrefix$tag" else tag
        actualTag = if (globalSuffix != null) "$actualTag$globalSuffix" else actualTag

        if (level >= curLevel) curPrinter?.invoke(Triple(level, actualTag, message)) ?: println("$actualTag : $message")

    }

}

fun ILog.log(level: Level, tag: String) =
    fun(message: String) =
        when (level) {
            Level.VERBOSE -> this.v(tag, message)
            Level.INFO -> this.i(tag, message)
            Level.DEBUG -> this.d(tag, message)
            Level.ERROR -> this.e(tag, message)
        }

class ILogImpl: AbsLogger()

object Logger : ILog by (ILogImpl().apply {
    this.setGlobalSuffix(" ==>")
        .setLevel(Level.INFO)
})
