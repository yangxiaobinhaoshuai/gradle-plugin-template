package me.yangxiaobin.lib.thread

import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.log.LogDelegate
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

private const val LOG_TAG = "TTF"

class TransformThreadFactory : ThreadFactory, LogAware by LogDelegate(InternalLogger, LOG_TAG) {

    private val threadPrefix = "-thd-"
    private val threadCounter = AtomicInteger(1)

    private val poolPrefix = LOG_TAG

    /**
     * TTF-thd-1
     */
    override fun newThread(r: Runnable): Thread {

        val thdName = "$poolPrefix$threadPrefix${threadCounter.getAndIncrement()}"
        val t = Thread(r, thdName)

        logV("Thread : $thdName created int TTF Factory.")

        return t
    }
}
