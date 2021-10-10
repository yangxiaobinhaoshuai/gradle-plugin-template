package me.yangxiaobin.androidapp

object Instrumentation {

    fun <T : Any?> recordDialog(possibleDialog: T): T {
        println("----> possible dialog :$possibleDialog")
        return possibleDialog
    }
}
