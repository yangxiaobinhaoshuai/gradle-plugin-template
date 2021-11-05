package me.yangxiaobin.lib.thread

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class TransformThreadFactory : ThreadFactory {

    private val logI = Logger.log(LogLevel.INFO, "TransformThreadFactory")

    private val threadPrefix = "-thread-"
    private val threadCounter = AtomicInteger(1)

    override fun newThread(r: Runnable): Thread =
        Thread("$POOL_PREFIX${poolCounter.getAndIncrement()}$threadPrefix${threadCounter.getAndIncrement()}").also { logI("Thread : ${it.name} created") }

    private companion object {
        private val poolCounter = AtomicInteger(1)
        private const val POOL_PREFIX = "Transport-pool-"
    }
}
