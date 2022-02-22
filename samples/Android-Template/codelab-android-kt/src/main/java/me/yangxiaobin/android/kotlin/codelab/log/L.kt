package me.yangxiaobin.android.kotlin.codelab.log

import android.util.Log
import me.yangxiaobin.kotlin.codelab.log.*

object L : AbsLogger() {

    override fun printLog(printer: IPrinter?, level: LogLevel, tag: String, message: String) {
        when (level) {
            LogLevel.VERBOSE -> Log.v(tag, message)
            LogLevel.INFO -> Log.i(tag, message)
            LogLevel.DEBUG -> Log.d(tag, message)
            LogLevel.ERROR -> Log.e(tag, message)
        }
    }

    override fun getConfig(): ILogConfig {
        return DefaultLogConfig(printer = null,minimumLevel = LogLevel.INFO)
    }
}
