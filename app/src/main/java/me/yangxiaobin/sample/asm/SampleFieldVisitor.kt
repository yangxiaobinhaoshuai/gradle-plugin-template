package me.yangxiaobin.sample.asm

import me.yangxiaobin.lib.asm.abs.AbsFieldVisitor
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Attribute
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.TypePath

class SampleFieldVisitor(fv: FieldVisitor) : AbsFieldVisitor(fv) {

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
