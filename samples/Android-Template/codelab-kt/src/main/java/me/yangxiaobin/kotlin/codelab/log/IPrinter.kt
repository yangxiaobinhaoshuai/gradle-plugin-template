package me.yangxiaobin.kotlin.codelab.log


interface IPrinter {
    fun printMessage(message: String)
}


object SystemOutPrinter : IPrinter {

    override fun printMessage(message: String) {
        println(message)
    }

}
