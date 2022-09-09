package me.yangxiaobin.lib.transform_v3


typealias MethodHandle = (Runnable) -> Unit

/**
 * Not thread safe
 */
object FunctionInvoker {

    private val functions: MutableMap<FunctionKey, Runnable> = mutableMapOf()

    private val hooks: MutableMap<FunctionKey, (Runnable) -> Unit> = mutableMapOf()

    private val invocationRecord = mutableListOf<FunctionKey>()

    fun  register(key: FunctionKey, r: Runnable) = apply {
        functions += key to r
    }

    fun hook(key: FunctionKey, methodHandle: MethodHandle) = apply {
        if (invocationRecord.contains(key)) throw IllegalStateException("U should register hook before ${key.name()} method call.")
        hooks += key to methodHandle
    }

    fun start() {
        functions.forEach { (key,func)->
            invocationRecord += key
            hooks[key]?.invoke(func) ?: func.run()
        }
    }

}

interface FunctionKey {

    fun name(): String


    data class KeyImpl(val name: String) : FunctionKey {

        override fun name(): String = name

    }

    companion object {

        fun of(name: String): FunctionKey {
            return KeyImpl(name)
        }
    }
}

