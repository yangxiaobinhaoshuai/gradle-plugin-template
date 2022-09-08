package me.yangxiaobin.lib

typealias GradleTransform = com.android.build.api.transform.Transform

typealias GradleTransformStatus = com.android.build.api.transform.Status

typealias GradleLogger = org.gradle.api.logging.Logger

typealias JUCExecutor = java.util.concurrent.Executor

typealias JUCExecutorService = java.util.concurrent.ExecutorService


typealias ActionLambda = () -> Unit

fun interface Action : Runnable, ActionLambda {

    override fun run()

    override fun invoke() {
        run()
    }
}

val EMPTY_ACTION: Action = Action {}

// Alias
typealias TransformAction = Action
