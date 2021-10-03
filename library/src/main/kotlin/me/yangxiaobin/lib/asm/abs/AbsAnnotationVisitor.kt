package me.yangxiaobin.lib.asm.abs

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

open class AbsAnnotationVisitor(av: AnnotationVisitor) : AnnotationVisitor(Opcodes.ASM9, av) {

    private val logV by lazy { Logger.log(LogLevel.VERBOSE, "AbsAv(${this.hashCode()})") }

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        logV("visit, name :$name, value :${value?.javaClass?.simpleName}")
    }

    override fun visitEnum(name: String?, descriptor: String?, value: String?) {
        super.visitEnum(name, descriptor, value)
        logV("visitEnum, name :$name, descriptor:$descriptor,value:$value")
    }

    override fun visitAnnotation(name: String?, descriptor: String?): AnnotationVisitor {
        return super.visitAnnotation(name, descriptor).also {
            logV("visitAnnotation,name:$name,descriptor:$descriptor")
        }
    }

    override fun visitArray(name: String?): AnnotationVisitor {
        return super.visitArray(name).also {
            logV("visitArray, name:$name")
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        logV("visitEnd")
    }
}
