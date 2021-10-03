package me.yangxiaobin.sample.asm

import me.yangxiaobin.lib.asm.abs.AbsAnnotationVisitor
import org.objectweb.asm.AnnotationVisitor

class SampleAnnotationVisitor(av:AnnotationVisitor) : AbsAnnotationVisitor(av) {

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
    }

    override fun visitEnum(name: String?, descriptor: String?, value: String?) {
        super.visitEnum(name, descriptor, value)
    }

    override fun visitAnnotation(name: String?, descriptor: String?): AnnotationVisitor {
        return super.visitAnnotation(name, descriptor)
    }

    override fun visitArray(name: String?): AnnotationVisitor {
        return super.visitArray(name)
    }

    override fun visitEnd() {
        super.visitEnd()
    }
}
