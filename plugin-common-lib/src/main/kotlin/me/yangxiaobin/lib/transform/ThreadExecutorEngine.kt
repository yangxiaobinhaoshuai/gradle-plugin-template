package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.Action
import me.yangxiaobin.lib.JUCExecutorService
import me.yangxiaobin.lib.executor.InternalExecutor
import me.yangxiaobin.lib.ext.neatName
import java.util.concurrent.Callable
import java.util.concurrent.Future

class ThreadExecutorEngine(private val executor: JUCExecutorService = InternalExecutor.fixed) :
    TransformEngine {

    override fun submitTransformEntry(transformers: List<Action>) {
        val map: List<Callable<Unit>> = transformers.map { Callable {
            println("---> task :${this.neatName} executed in :${Thread.currentThread().name}.")
            it.invoke()
        } }

        // Blocking here.
        executor.invokeAll(map).map(Future<Unit>::get)
    }

}
