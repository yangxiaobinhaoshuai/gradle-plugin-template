package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.JUCExecutorService
import me.yangxiaobin.lib.executor.InternalExecutor

class ThreadExecutorEngine(private val executor: JUCExecutorService = InternalExecutor.fixed) :
    TransformEngine {

    override fun submitTransformEntry(entry: TransformEntry) {

        executor.submit {}.get()

    }

}
