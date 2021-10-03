package me.yangxiaobin.lib.asm

import me.yangxiaobin.lib.asm.abs.AbsAdviceAdapter
import org.objectweb.asm.*
import org.objectweb.asm.util.*
import java.io.InputStream
import java.io.PrintWriter
import java.util.function.Function


fun ClassVisitor.wrappedWithTrace() = apply { TraceClassVisitor(this, PrintWriter(System.out)) }

fun ClassVisitor.wrappedWithCheck() = apply { CheckClassAdapter(this) }

fun MethodVisitor.wrappedWithTrace(printer: Printer = Textifier()) = apply { TraceMethodVisitor(this, printer) }

fun FieldVisitor.wrappedWithTrace(printer: Printer = Textifier()) = apply {
    TraceFieldVisitor(this,printer)
}

fun FieldVisitor.wrappedWithCheck() = apply {
    CheckFieldAdapter(this)
}

fun MethodVisitor.wrappedWithCheck() = apply { CheckMethodAdapter(this) }

fun MethodVisitor.wrappedWithAdvice(
    access: Int,
    name: String,
    desc: String,
    onEnter: (() -> Unit)? = null,
    onExit: (() -> Unit)? = null
) = apply { AbsAdviceAdapter(this, access, name, desc, onEnter, onExit) }

fun AnnotationVisitor.wrappedWithTrace() = apply { TraceAnnotationVisitor(this, Textifier()) }

fun AnnotationVisitor.wrappedWithCheck() = apply { CheckAnnotationAdapter(this) }

fun InputStream.applyAsm(func: (cw: ClassVisitor) -> ClassVisitor): ByteArray = Function<InputStream, ByteArray> {

    val cr = ClassReader(this)
    val cw = ClassWriter(cr, ClassWriter.COMPUTE_FRAMES)

    val cv = func.invoke(cw)


    cr.accept(
        cv.wrappedWithCheck().wrappedWithTrace(),
        ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES
    )

    cw.toByteArray()
}.apply(this)
