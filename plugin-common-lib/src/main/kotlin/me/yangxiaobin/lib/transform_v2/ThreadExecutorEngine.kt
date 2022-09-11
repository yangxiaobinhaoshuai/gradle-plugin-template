package me.yangxiaobin.lib.transform_v2

import me.yangxiaobin.lib.Action
import me.yangxiaobin.lib.JUCExecutorService
import me.yangxiaobin.lib.executor.InternalExecutor
import java.util.concurrent.Callable
import java.util.concurrent.Future

@Deprecated("see v3")
class ThreadExecutorEngine(private val executor: JUCExecutorService = InternalExecutor.fixed) :
    TransformEngine {

    override fun submitTransformAction(transformActions: List<Action>) {

        val map: List<Callable<Unit>> = transformActions.map { Callable {
            // TODO
            //println("---> task :${this.neatName} executed in :${Thread.currentThread().name}.")
            it.invoke()
        } }

        // Blocking here.
        executor.invokeAll(map).map(Future<Unit>::get)
    }

}
