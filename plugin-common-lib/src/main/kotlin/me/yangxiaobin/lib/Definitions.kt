package me.yangxiaobin.lib

import java.util.concurrent.Callable

typealias GradleTransform = com.android.build.api.transform.Transform

typealias GradleTransformStatus = com.android.build.api.transform.Status

typealias GradleLogger = org.gradle.api.logging.Logger

typealias JUCExecutor = java.util.concurrent.Executor

typealias JUCExecutorService = java.util.concurrent.ExecutorService


typealias TypedActionLambda<T> = () -> T

fun interface TypedAction<T> : Callable<T> ,TypedActionLambda<T>{

    override fun call(): T = invoke()
}

fun interface Action : TypedAction<Unit>, Runnable {

    override fun call() = run()

    override fun run() = invoke()
}

typealias TransformAction = Action

typealias TypedTransformAction<T> = TypedAction<T>
