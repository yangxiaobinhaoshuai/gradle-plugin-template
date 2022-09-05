package me.yangxiaobin.lib.asm.log

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.log
import org.objectweb.asm.AnnotationVisitor

class LoggableAnnotationVisitor(api: Int, av: AnnotationVisitor) : AnnotationVisitor(api, av) {

    private val logV by lazy { InternalLogger.log(LogLevel.VERBOSE, "AbsAv(${this.hashCode()})") }

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
