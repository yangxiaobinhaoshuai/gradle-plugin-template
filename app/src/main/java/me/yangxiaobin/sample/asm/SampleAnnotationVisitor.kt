package me.yangxiaobin.sample.asm

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

class SampleAnnotationVisitor(av: AnnotationVisitor) : AnnotationVisitor(Opcodes.ASM9, av) {

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
