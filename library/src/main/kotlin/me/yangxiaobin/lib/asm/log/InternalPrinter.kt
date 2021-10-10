package me.yangxiaobin.lib.asm.log

import org.objectweb.asm.util.Textifier

class InternalPrinter(api: Int) : Textifier(api) {

    override fun visitMethodEnd() {
        super.visitMethodEnd()
        println("\r")
        println(getText())
        println("\r")
    }

}
