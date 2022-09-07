package me.yangxiaobin.lib.executor

import me.yangxiaobin.lib.ext.CPU_COUNT
import me.yangxiaobin.lib.thread.TransformThreadFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ThreadFactory

@Suppress("MemberVisibilityCanBePrivate")
object InternalExecutor {

    val defaultThdFactory: ThreadFactory by lazy { TransformThreadFactory() }

    val fixed: ExecutorService by lazy { Executors.newFixedThreadPool(CPU_COUNT * 2, defaultThdFactory) }

    val forkJoin: ForkJoinPool by lazy { ForkJoinPool.commonPool() }
}
