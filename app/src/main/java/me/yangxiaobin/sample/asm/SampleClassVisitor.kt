package me.yangxiaobin.sample.asm

import me.yangxiaobin.lib.asm.abs.AbsClassVisitor
import me.yangxiaobin.lib.asm.annotation.TimeAnalysisMethodVisitor
import me.yangxiaobin.lib.asm.wrappedWithTrace
import org.objectweb.asm.*
import org.objectweb.asm.util.ASMifier

class SampleClassVisitor(cv: ClassVisitor) : AbsClassVisitor(cv) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitSource(source: String?, debug: String?) {
        super.visitSource(source, debug)
    }

    override fun visitModule(name: String?, access: Int, version: String?): ModuleVisitor {
        return super.visitModule(name, access, version)
    }

    override fun visitNestHost(nestHost: String?) {
        super.visitNestHost(nestHost)
    }

    override fun visitOuterClass(owner: String?, name: String?, descriptor: String?) {
        super.visitOuterClass(owner, name, descriptor)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val originalAv = super.visitAnnotation(descriptor, visible)
        return SampleAnnotationVisitor(originalAv).wrappedWithTrace()
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

    override fun visitNestMember(nestMember: String?) {
        super.visitNestMember(nestMember)
    }

    override fun visitPermittedSubclass(permittedSubclass: String?) {
        super.visitPermittedSubclass(permittedSubclass)
    }

    override fun visitInnerClass(name: String?, outerName: String?, innerName: String?, access: Int) {
        super.visitInnerClass(name, outerName, innerName, access)
    }

    override fun visitRecordComponent(name: String?, descriptor: String?, signature: String?): RecordComponentVisitor {
        return super.visitRecordComponent(name, descriptor, signature)
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {

        val originalFv =super.visitField(access, name, descriptor, signature, value)

        return SampleFieldVisitor(originalFv).wrappedWithTrace()
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        println("----> method :$name/$descriptor")
//        return super.visitMethod(access, name, descriptor, signature, exceptions)
        val originalMv = super.visitMethod(access, name, descriptor, signature, exceptions)

        //return SampleMethodVisitor(originalMv).wrappedWithTrace()
        return TimeAnalysisMethodVisitor(originalMv).wrappedWithTrace()
    }

    override fun visitEnd() {
        super.visitEnd()
    }
}
