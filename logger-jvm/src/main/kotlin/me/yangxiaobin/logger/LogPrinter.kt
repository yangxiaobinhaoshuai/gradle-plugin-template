package me.yangxiaobin.logger


typealias LogPrinterFunc = (Pair<String, String>) -> Unit

interface LogPrinter {

    fun print(tag: String, message: String)
}
