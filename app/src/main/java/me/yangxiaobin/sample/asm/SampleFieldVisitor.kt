package me.yangxiaobin.sample.asm

import org.objectweb.asm.*

class SampleFieldVisitor(fv: FieldVisitor) : FieldVisitor(Opcodes.ASM9, fv) {

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitTypeAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible)
    }

    override fun visitAttribute(attribute: Attribute?) {
        super.visitAttribute(attribute)
    }

    override fun visitEnd() {
        super.visitEnd()
    }
}
