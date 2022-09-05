package me.yangxiaobin.lib.asm.log

import me.yangxiaobin.lib.asm.constant.ASM_API
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.log
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class LoggableAdviceAdapter(
    api: Int = ASM_API,
    mv: MethodVisitor,
    access: Int,
    name: String,
    desc: String,
    private val enterFunc: (() -> Unit)? = null,
    private val exitFunc: (() -> Unit)? = null,
) : AdviceAdapter(api, mv, access, name, desc) {


    private val logV by lazy { InternalLogger.log(LogLevel.VERBOSE, "AbsAdvice(${this.hashCode()})") }

    override fun onMethodEnter() {
        super.onMethodEnter()
        logV("onMethodEnter")
        enterFunc?.invoke()
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        logV("onMethodExit")
        exitFunc?.invoke()
    }
}
