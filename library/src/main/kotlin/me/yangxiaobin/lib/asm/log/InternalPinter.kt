package me.yangxiaobin.lib.asm.log

import org.objectweb.asm.util.ASMifier
import org.objectweb.asm.util.Textifier

class InternalTextifier(api: Int) : Textifier(api) {

    override fun visitMethodEnd() {
        super.visitMethodEnd()
        println("\r")
        println(getText())
        println("\r")
    }

}

class InternalASMifier(api: Int) : ASMifier(api, "asmifier", 0) {

    override fun visitMethodEnd() {
        super.visitMethodEnd()
        println("\r")
        println(getText())
        println("\r")
    }

}
