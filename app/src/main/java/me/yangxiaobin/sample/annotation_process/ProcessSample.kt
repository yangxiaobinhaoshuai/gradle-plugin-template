package me.yangxiaobin.sample.annotation_process

class ProcessSample {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("----> ProcessSample main args :$args")
            Foo().foo()
        }
    }
}
