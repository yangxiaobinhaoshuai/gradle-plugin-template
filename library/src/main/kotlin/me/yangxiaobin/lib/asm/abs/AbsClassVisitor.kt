package me.yangxiaobin.lib.asm.abs

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.objectweb.asm.*

open class AbsClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM9, cv) {

    private val logV by lazy { Logger.log(LogLevel.VERBOSE, "AbsCv(${this.hashCode()})") }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        logV(
            """
            visit :
            version: $version
            access : $access
            name   : $name
            signature: $signature
            superName: $superName
            interfaces:${interfaces?.contentToString()}
        """.trimIndent()
        )
    }

    override fun visitSource(source: String?, debug: String?) {
        super.visitSource(source, debug)
        logV("visitSource, source:$source, debug:$debug")
    }

    override fun visitModule(name: String?, access: Int, version: String?): ModuleVisitor {
        return super.visitModule(name, access, version).also {
            logV("visitModule, name :$name , access :$access, version : $version")
        }
    }

    override fun visitNestHost(nestHost: String?) {
        super.visitNestHost(nestHost)
        logV("visitNestHost , nestHost :$nestHost")
    }

    override fun visitOuterClass(owner: String?, name: String?, descriptor: String?) {
        super.visitOuterClass(owner, name, descriptor)
        logV("visitOuterClass, owner :$owner ,name :$name ,descriptor :$descriptor")
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitAnnotation(descriptor, visible).also {
            logV("visitAnnotation, descriptor :$descriptor, visible :$visible")
        }
    }

    override fun visitTypeAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible).also {
            logV(
                """
                visitTypeAnnotation:
                typeRef :$typeRef
                typePath:$typePath
                descriptor:$descriptor
                visible:$visible
            """.trimIndent()
            )
        }
    }

    override fun visitAttribute(attribute: Attribute?) {
        super.visitAttribute(attribute)
        logV("visitAttribute, attribute :$attribute")
    }

    override fun visitNestMember(nestMember: String?) {
        super.visitNestMember(nestMember)
        logV("")
    }

    override fun visitPermittedSubclass(permittedSubclass: String?) {
        super.visitPermittedSubclass(permittedSubclass)
        logV("visitPermittedSubclass, permittedSubclass:$permittedSubclass")
    }

    override fun visitInnerClass(name: String?, outerName: String?, innerName: String?, access: Int) {
        super.visitInnerClass(name, outerName, innerName, access)
        logV(
            """
            visitInnerClass:
            name:$name
            outerName :$outerName
            innerName :$innerName
            access :$access
        """.trimIndent()
        )
    }

    override fun visitRecordComponent(name: String?, descriptor: String?, signature: String?): RecordComponentVisitor {
        return super.visitRecordComponent(name, descriptor, signature).also {
            logV("visitRecordComponent,name :$name,descriptor:$descriptor,signature:$signature")
        }
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return super.visitField(access, name, descriptor, signature, value).also {
            logV(
                """
                visitField:
                access:$access
                name:$name
                descriptor:$descriptor
                signature:$signature
                value:$value
            """.trimIndent()
            )
        }
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return super.visitMethod(access, name, descriptor, signature, exceptions).also {
            logV(
                """
                visitMethod:
                access:$access
                name :$name
                descriptor:$descriptor
                signature:$signature
                exception:$exceptions
            """.trimIndent()
            )
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        logV("visitEnd")
    }
}
