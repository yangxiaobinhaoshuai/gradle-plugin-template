package me.yangxiaobin.sample.asm

class ClazzStub : Runnable, java.util.function.LongConsumer {

    private val stringField = "sss"
    private val intField = 1024
    private val nullableStub: me.yangxiaobin.sample.asm.ClazzStub? = null
    private val lazyStub by lazy { me.yangxiaobin.sample.asm.ClazzStub() }

    init {
        println("init block")
    }

    override fun run() {
        println("run run run")
    }

    override fun accept(value: Long) {
        println("accept accept accept")
    }
}
