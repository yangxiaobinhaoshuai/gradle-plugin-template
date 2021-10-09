package me.yangxiaobin.lib.asm.log

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import me.yangxiaobin.lib.log.log
import org.objectweb.asm.*

class LoggableMethodVisitor(api: Int = Opcodes.ASM9, mv: MethodVisitor) : MethodVisitor(api, mv) {

    private val logV by lazy { Logger.log(LogLevel.VERBOSE, "AbsMv(${this.hashCode()})") }

    override fun visitParameter(name: String?, access: Int) {
        super.visitParameter(name, access)
        logV("visitParameter,name:$name,access:$access")
    }

    override fun visitAnnotationDefault(): AnnotationVisitor {
        return super.visitAnnotationDefault().also {
            logV("visitAnnotationDefault")
        }
    }

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

    override fun visitAnnotableParameterCount(parameterCount: Int, visible: Boolean) {
        super.visitAnnotableParameterCount(parameterCount, visible)
        logV("visitAnnotableParameterCount,parameterCount:$parameterCount,visible:$visible")
    }

    override fun visitParameterAnnotation(parameter: Int, descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitParameterAnnotation(parameter, descriptor, visible).also {
            logV(
                """
                visitParameterAnnotation:
                parameter:$parameter
                descriptor:$descriptor
                visible:$visible
            """.trimIndent()
            )
        }
    }

    override fun visitAttribute(attribute: Attribute?) {
        super.visitAttribute(attribute)
        logV("visitAttribute,attribute:$attribute")
    }

    override fun visitCode() {
        super.visitCode()
        logV("visitCode")
    }

    override fun visitFrame(type: Int, numLocal: Int, local: Array<out Any>?, numStack: Int, stack: Array<out Any>?) {
        super.visitFrame(type, numLocal, local, numStack, stack)
        logV(
            """
            visitFrame:
            type:$type
            numLocal:$numLocal
            local:$local
            numStack:$numStack
            stack:$stack
        """.trimIndent()
        )
    }

    override fun visitInsn(opcode: Int) {
        super.visitInsn(opcode)
        logV("visitInsn,opcode:$opcode")
    }

    override fun visitIntInsn(opcode: Int, operand: Int) {
        super.visitIntInsn(opcode, operand)
        logV("visitIntInsn,opcode:$opcode, operand:$operand")
    }

    override fun visitVarInsn(opcode: Int, `var`: Int) {
        super.visitVarInsn(opcode, `var`)
        logV("visitVarInsn,opcode:$opcode, var :$`var`")
    }

    override fun visitTypeInsn(opcode: Int, type: String?) {
        super.visitTypeInsn(opcode, type)
        logV("visitTypeInsn,opcode:$opcode,type:$type")
    }

    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        super.visitFieldInsn(opcode, owner, name, descriptor)
        logV(
            """
            visitFieldInsn:
            opcode:$opcode
            owner:$owner
            name:$name
            descriptor:$descriptor
        """.trimIndent()
        )
    }

    override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        super.visitMethodInsn(opcode, owner, name, descriptor)
        logV(
            """
            visitMethodInsn:
            opcode:$opcode
            owner:$owner
            name:$name
            descriptor:$descriptor
        """.trimIndent()
        )
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        logV(
            """
            visitMethodInsn:
            opcode:$opcode
            owner:$owner
            name:$name
            descriptor:$descriptor
            isInterface:$isInterface
        """.trimIndent()
        )
    }

    override fun visitInvokeDynamicInsn(
        name: String?,
        descriptor: String?,
        bootstrapMethodHandle: Handle?,
        vararg bootstrapMethodArguments: Any?
    ) {
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)
        logV(
            """
            visitInvokeDynamicInsn:
            name:$name
            descriptor:$descriptor
            bootstrapMethodHandle:$bootstrapMethodHandle
            bootstrapMethodArguments:${bootstrapMethodArguments.size}
        """.trimIndent()
        )
    }

    override fun visitJumpInsn(opcode: Int, label: Label?) {
        super.visitJumpInsn(opcode, label)
        logV("visitJumpInsn,opcode:$opcode,label:$label")
    }

    override fun visitLabel(label: Label?) {
        super.visitLabel(label)
        logV("visibleLabel,label:$label")
    }

    override fun visitLdcInsn(value: Any?) {
        super.visitLdcInsn(value)
        logV("visitLdcInsn,value:$value")
    }

    override fun visitIincInsn(`var`: Int, increment: Int) {
        super.visitIincInsn(`var`, increment)
        logV("visitIincInsn,var:$`var`,increment:$increment")
    }

    override fun visitTableSwitchInsn(min: Int, max: Int, dflt: Label?, vararg labels: Label?) {
        super.visitTableSwitchInsn(min, max, dflt, *labels)
        logV(
            """
            visitTableSwitchInsn:
            min:$min
            max:$max
            dflt:$dflt
            labels:${labels.size}
        """.trimIndent()
        )
    }

    override fun visitLookupSwitchInsn(dflt: Label?, keys: IntArray?, labels: Array<out Label>?) {
        super.visitLookupSwitchInsn(dflt, keys, labels)
        logV("visitLookupSwitchInsn,dflt:$dflt,keys:$keys,labels:${labels?.size}")
    }

    override fun visitMultiANewArrayInsn(descriptor: String?, numDimensions: Int) {
        super.visitMultiANewArrayInsn(descriptor, numDimensions)
        logV("visitMultiANewArrayInsn,descriptor:$descriptor,numDimensions:$numDimensions")
    }

    override fun visitInsnAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible).also {
            logV(
                """
                visitInsnAnnotation:
                typeRef:$typeRef
                typePath:$typePath
                descriptor:$descriptor
                visible:$visible
            """.trimIndent()
            )
        }
    }

    override fun visitTryCatchBlock(start: Label?, end: Label?, handler: Label?, type: String?) {
        super.visitTryCatchBlock(start, end, handler, type)
        logV(
            """
            visitTryCatchBlock:
            start:$start
            end:$end
            handler:$handler
            type:$type
        """.trimIndent()
        )
    }

    override fun visitTryCatchAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        return super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible).also {
            logV(
                """
                visitTryCatchAnnotation:
                typeRef:$typeRef
                typePath:$typePath
                descriptor:$descriptor
                visible:$visible
            """.trimIndent()
            )
        }
    }

    override fun visitLocalVariable(
        name: String?,
        descriptor: String?,
        signature: String?,
        start: Label?,
        end: Label?,
        index: Int
    ) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index)
        logV(
            """
                visitLocalVariable:
                name:$name
                descriptor:$descriptor
                signature:$signature
                start:$start
                end:$end
                index:$index
            """.trimIndent()
        )
    }

    override fun visitLocalVariableAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        start: Array<out Label>?,
        end: Array<out Label>?,
        index: IntArray?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible).also {
            logV(
                """
                visitLocalVariableAnnotation:
                typeRef:$typeRef
                typePath:$typePath
                start:$start
                end:$end
                index:$index
                descriptor:$descriptor
                visible:$visible
            """.trimIndent()
            )
        }
    }

    override fun visitLineNumber(line: Int, start: Label?) {
        super.visitLineNumber(line, start)
        logV("visitLineNumber,line:$line, start:$start")
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack, maxLocals)
        logV("visitMaxs,maxStack:$maxStack, maxLocals:$maxLocals")
    }

    override fun visitEnd() {
        super.visitEnd()
        logV("visitEnd")
    }
}
