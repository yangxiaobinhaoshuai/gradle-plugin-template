package me.yangxiaobin.lib.asm.api

import me.yangxiaobin.lib.asm.annotation.MethodAdviceVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class DefaultClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM9, cv) {

    private var classFileName = ""


    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        classFileName = name ?: return
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val superMv = super.visitMethod(access, name, descriptor, signature, exceptions)

        requireNotNull(name) { return superMv }
        requireNotNull(descriptor) { return superMv }

        return MethodAdviceVisitor(superMv, classFileName, access, name, descriptor)
    }

}
