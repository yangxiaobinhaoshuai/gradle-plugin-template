//package me.yangxiaobin.sample.asm
//
//import me.yangxiaobin.lib.asm.annotation.MethodAdvice
//import me.yangxiaobin.lib.asm.annotation.MethodAdviceInstrument
//import me.yangxiaobin.lib.asm.annotation.TimeAnalysis
//
//class ClazzStub : Runnable, java.util.function.LongConsumer {
//
//    private val stringField = "sss"
//    private val intField = 1024
//    private val nullableStub: ClazzStub? = null
//    private val runnable = Runnable {
//        Thread.sleep(200)
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
//    @MethodAdvice
//    fun show() {
//        //val p = MethodAdviceInstrument.before()
//
//        println("----> I'm showing.")
//        runnable.run()
//
//        //MethodAdviceInstrument.after(p)
//    }
//}
