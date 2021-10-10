package me.yangxiaobin.lib.asm.annotation

import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter


@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MethodAdvice


interface AdviceInstrument {

    fun before(
        classFileName: String,
        methodAccess: Int,
        methodName: String,
        methodDesc: String
    ): Any?

    fun after(
        classFileName: String,
        methodAccess: Int,
        methodName: String,
        methodDesc: String,
        param: Any?
    )

}

object MethodAdviceInstrument : AdviceInstrument {

    override fun before(
        classFileName: String,
        methodAccess: Int,
        methodName: String,
        methodDesc: String
    ): Any? {
        println(
            """
            before :
            classFileName:$classFileName
            methodAccess:$methodAccess
            methodName:$methodName
            methodDesc:$methodDesc
        """.trimIndent()
        )
        return System.currentTimeMillis()
    }

    override fun after(
        classFileName: String,
        methodAccess: Int,
        methodName: String,
        methodDesc: String,
        param: Any?
    ) {
        println(
            """
            after :
            classFileName:$classFileName
            methodAccess:$methodAccess
            methodName:$methodName
            methodDesc:$methodDesc
        """.trimIndent()
        )
        val t1 = (param as? Long) ?: return
        val difference = System.currentTimeMillis() - t1
        println("----> aafdsa asfa sdifference :$difference")
    }

}

class MethodAdviceVisitor(
    api:Int,
    mv: MethodVisitor?,
    private val classFileName: String,
    methodAccess: Int,
    methodName: String,
    methodDesc: String
) : AdviceAdapter(api, mv, methodAccess, methodName, methodDesc) {

    private var shouldInsert = false
    private var paramIndex = 0


    private val stringDesc = Type.getDescriptor(String::class.java)
    private val intDesc = Type.getDescriptor(Int::class.java)

    private val instrumentOwner = Type.getInternalName(MethodAdviceInstrument::class.java)
    private val instrumentDesc = Type.getDescriptor(MethodAdviceInstrument::class.java)

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        shouldInsert = descriptor == Type.getDescriptor(MethodAdvice::class.java)
        return super.visitAnnotation(descriptor, visible)
    }

    override fun onMethodEnter() {
        super.onMethodEnter()

        if (!shouldInsert) return
        mv.visitFieldInsn(Opcodes.GETSTATIC, instrumentOwner, "INSTANCE", instrumentDesc)

        mv.visitLdcInsn(classFileName)
        mv.visitLdcInsn(methodAccess)
        mv.visitLdcInsn(name)
        mv.visitLdcInsn(methodDesc)

        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            instrumentOwner,
            "before",
            "($stringDesc$intDesc$stringDesc$stringDesc)Ljava/lang/Object;",
            false
        )
        paramIndex = newLocal(Type.getType(Object::class.java))
        mv.visitVarInsn(Opcodes.ASTORE, paramIndex)
    }


    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)

        if (!shouldInsert) return

        mv.visitFieldInsn(Opcodes.GETSTATIC, instrumentOwner, "INSTANCE", instrumentDesc)
        mv.visitLdcInsn(classFileName)
        mv.visitLdcInsn(methodAccess)
        mv.visitLdcInsn(name)
        mv.visitLdcInsn(methodDesc)
        mv.visitVarInsn(Opcodes.ALOAD, paramIndex)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            instrumentOwner,
            "after",
            "(${stringDesc}${intDesc}${stringDesc}${stringDesc}Ljava/lang/Object;)V",
            false
        )
    }

}
