package me.yangxiaobin.lib.asm.annotation

import me.yangxiaobin.lib.asm.abs.AbsAdviceAdapter
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type


@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TimeAnalysis


class TimeAnalysisMethodVisitor(access: Int, name: String, desc: String, mv: MethodVisitor) :
    AbsAdviceAdapter(mv, access, name, desc) {

    private var insert = false
    private var t1Index: Int = 0
    private var t2Index: Int = 0

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        insert = descriptor == Type.getDescriptor(TimeAnalysis::class.java)
        return super.visitAnnotation(descriptor, visible)
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        if (insert) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            t1Index = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, t1Index);
        }
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        if (insert) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitVarInsn(LLOAD, t1Index);
            mv.visitInsn(LSUB);

            t2Index = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, t2Index)

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

            mv.visitVarInsn(LLOAD, t2Index)

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);

        }


    }
}
