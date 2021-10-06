package me.yangxiaobin.lib.asm.log

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.objectweb.asm.*

class LoggableFieldVisitor(fv: FieldVisitor) : FieldVisitor(Opcodes.ASM9, fv) {

    private val logV by lazy { Logger.log(LogLevel.VERBOSE, "AbsFv(${this.hashCode()})") }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitAnnotation(descriptor, visible).also {
            logV("visitAnnotation,descriptor:$descriptor,visible:$visible")
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
                typeRef:$typeRef
                typePath:$typePath
                descriptor:$descriptor
                visible:$visible
            """.trimIndent()
            )
        }
    }

    override fun visitAttribute(attribute: Attribute?) {
        super.visitAttribute(attribute)
        logV("visitAttribute,attr:$attribute")
    }

    override fun visitEnd() {
        super.visitEnd()
        logV("visitEnd")
    }
}
