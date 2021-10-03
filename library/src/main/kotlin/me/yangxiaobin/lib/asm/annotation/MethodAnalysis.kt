package me.yangxiaobin.lib.asm.annotation

import me.yangxiaobin.lib.asm.abs.AbsMethodVisitor
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type


@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TimeAnalysis


class TimeAnalysisMethodVisitor(mv: MethodVisitor) : AbsMethodVisitor(mv) {

    private var insert = false

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        insert = descriptor == Type.getDescriptor(TimeAnalysis::class.java)
        println("---> should insert :$insert")
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitCode() {
        super.visitCode()
        val owner = Type.getInternalName(System::class.java)
        val name = Type.getInternalName(System::out::class.java)
        val desc = Type.getDescriptor(System::out::class.java)
        println("----> enter enter :$owner   $name   $desc")
//        mv.visitFieldInsn(Opcodes.GETSTATIC,Type.getInternalName(System::class.java),Type)

        if (insert) {
            mv.visitLdcInsn("ana ana ana");
            mv.visitVarInsn(ASTORE, 1);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 2);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
        }
    }

    override fun visitEnd() {
        super.visitEnd()

        val owner = Type.getInternalName(System::class.java)
        val name = "out"
        val desc = Type.getDescriptor(System.out::class.java)
        println("----> exit :$owner  $name  $desc")
    }
}
