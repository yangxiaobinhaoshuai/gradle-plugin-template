//package me.yangxiaobin.sample.asm
//
//import me.yangxiaobin.lib.asm.annotation.TimeAnalysis
//
//class ClazzStub : Runnable, java.util.function.LongConsumer {
//
//    private val stringField = "sss"
//    private val intField = 1024
//    private val nullableStub: ClazzStub? = null
//    private val runnable = Runnable {
//        println("---> I'm a runnable run.")
//    }
//
//    init {
//        println("ClazzStub init block")
//    }
//
//    override fun run() {
//        println("run run run")
//    }
//
//    override fun accept(value: Long) {
//        println("accept accept accept")
//    }
//
//    @TimeAnalysis
//    fun show() {
//        println("----> I'm showing.")
//        runnable.run()
//    }
//}
