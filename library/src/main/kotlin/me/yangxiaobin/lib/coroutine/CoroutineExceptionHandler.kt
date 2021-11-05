package me.yangxiaobin.lib.coroutine

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext


private const val TAG = "CustomCoroutineHandler"

val coroutineHandler: AbstractCoroutineContextElement by lazy {

    object : AbstractCoroutineContextElement(CoroutineExceptionHandler),
        CoroutineExceptionHandler {

        private val logE = Logger.log(LogLevel.ERROR,TAG)

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            logE(
                """
                    ${context[Job]} occurred exception : $exception
                    stacktrace see below :
                    ${exception.stackTraceToString()}
                """.trimIndent()
            )
        }
    }
}

val transportCoroutineName = CoroutineName("Transport-Coroutine")
