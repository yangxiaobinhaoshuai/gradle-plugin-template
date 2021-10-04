package me.yangxiaobin.lib.asm.api

import me.yangxiaobin.lib.asm.abs.AbsAdviceAdapter
import org.objectweb.asm.*
import org.objectweb.asm.util.*
import java.io.InputStream
import java.io.PrintWriter
import java.util.function.Function


fun ClassVisitor.wrappedWithTrace() = run { TraceClassVisitor(this, PrintWriter(System.out)) }

fun ClassVisitor.wrappedWithCheck() = run { CheckClassAdapter(this) }

fun MethodVisitor.wrappedWithTrace(printer: Printer = Textifier()) = run { TraceMethodVisitor(this, printer) }

fun FieldVisitor.wrappedWithTrace(printer: Printer = Textifier()) = run {
    TraceFieldVisitor(this, printer)
}

fun FieldVisitor.wrappedWithCheck() = run {
    CheckFieldAdapter(this)
}

fun MethodVisitor.wrappedWithCheck() = run { CheckMethodAdapter(this) }

fun MethodVisitor.wrappedWithAdvice(
    access: Int,
    name: String,
    desc: String,
    onEnter: (() -> Unit)? = null,
    onExit: (() -> Unit)? = null
) = run { AbsAdviceAdapter(this, access, name, desc, onEnter, onExit) }

fun AnnotationVisitor.wrappedWithTrace() = run { TraceAnnotationVisitor(this, Textifier()) }

fun AnnotationVisitor.wrappedWithCheck() = run { CheckAnnotationAdapter(this) }

fun InputStream.applyAsm(func: (cw: ClassVisitor) -> ClassVisitor = { DefaultClassVisitor(it) })
        : ByteArray = Function<InputStream, ByteArray> {

    val cr = ClassReader(this)
    val cw = ClassWriter(cr, ClassWriter.COMPUTE_FRAMES)

    val cv = func.invoke(cw.wrappedWithTrace())

    val parsingOptions = ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES

    cr.accept(cv.wrappedWithTrace(), 0)

    cw.toByteArray()
}.apply(this)
