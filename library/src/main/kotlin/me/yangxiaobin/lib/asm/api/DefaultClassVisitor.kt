package me.yangxiaobin.lib.asm.api

import me.yangxiaobin.lib.asm.annotation.MethodAdviceVisitor
import me.yangxiaobin.lib.asm.log.InternalASMifier
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

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

        return superMv
        //println("---> method :$name")
//        return MethodAdviceVisitor(api, superMv, classFileName, access, name, descriptor)
//            //.let { if (name == "init\$lambda-0") it.wrappedWithTrace() else it }
//            .wrappedWithTreeAdapter(
//                api,
//                access,
//                name,
//                descriptor,
//                signature,
//                exceptions
//            ) {
//                val insnList = it.instructions
//                val iter = insnList.iterator()
//                while (iter.hasNext()) {
//                    val insn = iter.next()
//                    if (insn is MethodInsnNode && insn.name == "show") {
//
//                        //GETSTATIC me/yangxiaobin/androidapp/Instrumentation.INSTANCE : Lme/yangxiaobin/androidapp/Instrumentation;
//                        //ALOAD 2
//                        //INVOKEVIRTUAL me/yangxiaobin/androidapp/Instrumentation.recordDialog (Ljava/lang/Object;)Ljava/lang/Object;
//                        //CHECKCAST android/app/Dialog
//                        //INVOKEVIRTUAL android/app/Dialog.show ()V
//
//                        var nearestLineNumNode = insn.previous
//
//                        while (nearestLineNumNode!=null && nearestLineNumNode !is LineNumberNode){
//                            nearestLineNumNode = nearestLineNumNode.previous
//                        }
//                        println("----> nearest line nume :${(nearestLineNumNode as? LineNumberNode)?.line}")
//
//                        val aLoadInsn: AbstractInsnNode = insn.previous
//
//                        println("---> aloadInsn ,${aLoadInsn.opcode}:${aLoadInsn.opcode == Opcodes.ALOAD}")
//
//                        val getFieldInsnNode = FieldInsnNode(
//                            Opcodes.GETSTATIC,
//                            "me/yangxiaobin/androidapp/Instrumentation",
//                            "INSTANCE",
//                            "Lme/yangxiaobin/androidapp/Instrumentation;"
//                        )
//
//                        val invokeSpecialNode = MethodInsnNode(Opcodes.INVOKEVIRTUAL,"me/yangxiaobin/androidapp/Instrumentation","recordDialog","(Ljava/lang/Object;)Ljava/lang/Object;",false)
//
//                        val checkCastInsnNode = TypeInsnNode(Opcodes.CHECKCAST,insn.owner)
//
//                        insnList.insertBefore(aLoadInsn,getFieldInsnNode)
//                        insnList.insertBefore(insn,checkCastInsnNode)
//                        insnList.insertBefore(checkCastInsnNode,invokeSpecialNode)
////                        println("-----> find show !!! ${insn.previous} , ${insn.next.opcode == Opcodes.RETURN}")
//                    }
//                }
//            }
    }

}
