package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.JUCExecutorService
import me.yangxiaobin.lib.ext.CPU_COUNT
import me.yangxiaobin.lib.thread.TransformThreadFactory
import java.util.concurrent.Executors
import javax.xml.transform.TransformerFactory

private val defaultThdFactory = TransformThreadFactory()

private val fixedExecutor = Executors.newFixedThreadPool(CPU_COUNT, defaultThdFactory)

class ThreadExecutorEngine(private val executor: JUCExecutorService = fixedExecutor) : TransformEngine {

    override fun submitTransformEntry(entry: TransformEntry) {

        executor.submit {

        }.get()

    }

}
