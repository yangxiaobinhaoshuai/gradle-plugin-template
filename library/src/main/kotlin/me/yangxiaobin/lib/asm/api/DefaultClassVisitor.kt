package me.yangxiaobin.lib.asm.api

import me.yangxiaobin.lib.asm.annotation.MethodAdviceVisitor
import me.yangxiaobin.lib.asm.log.InternalPrinter
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class DefaultClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {

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
        exceptions: Array<String>?
    ): MethodVisitor? {

        val superMv: MethodVisitor? = super.visitMethod(access, name, descriptor, signature, exceptions)

        requireNotNull(name) { return superMv }
        requireNotNull(descriptor) { return superMv }

//        println("---> method :$name")
        return MethodAdviceVisitor(api, superMv, classFileName, access, name, descriptor)
            .wrappedWithTreeAdapter(
                api,
                access,
                name,
                descriptor,
                signature,
                exceptions
            ) {
            }
    }

}
