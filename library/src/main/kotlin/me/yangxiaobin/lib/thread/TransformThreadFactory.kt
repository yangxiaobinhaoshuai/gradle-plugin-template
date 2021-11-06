package me.yangxiaobin.lib.thread

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class TransformThreadFactory : ThreadFactory {

    private val logV = Logger.log(LogLevel.VERBOSE, "TransformThreadFactory")

    private val threadPrefix = "-thread-"
    private val threadCounter = AtomicInteger(1)

    private val poolPrefix = "Transport-pool-" + poolCounter.getAndIncrement()

    override fun newThread(r: Runnable): Thread =
        Thread("$poolPrefix$threadPrefix${threadCounter.getAndIncrement()}").also { logV("Thread : ${it.name} created") }

    private companion object {
        private val poolCounter = AtomicInteger(1)

    }
}
