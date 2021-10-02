package me.yangxiaobin.lib.asm

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.objectweb.asm.*

class AbsClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM9, cv) {

    private val logI = Logger.log(LogLevel.DEBUG,"AbsClassVisitor")

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
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    override fun visitEnd() {
        super.visitEnd()
    }
}
