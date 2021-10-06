package me.yangxiaobin.lib.asm.api

import me.yangxiaobin.lib.asm.log.LoggableAdviceAdapter
import me.yangxiaobin.lib.asm.log.LoggableClassVisitor
import me.yangxiaobin.lib.asm.log.LoggableFieldVisitor
import me.yangxiaobin.lib.asm.log.LoggableMethodVisitor
import org.objectweb.asm.*
import org.objectweb.asm.util.*
import java.io.InputStream
import java.io.PrintWriter
import java.util.function.Function


fun ClassVisitor.wrappedWithTrace() = TraceClassVisitor(this, PrintWriter(System.out))

fun ClassVisitor.wrappedWithCheck() = CheckClassAdapter(this)

fun ClassVisitor.wrappedWithLog() = LoggableClassVisitor(this)

fun MethodVisitor.wrappedWithTrace(printer: Printer = Textifier()) = TraceMethodVisitor(this, printer)

fun FieldVisitor.wrappedWithTrace(printer: Printer = Textifier()) = TraceFieldVisitor(this, printer)

fun FieldVisitor.wrappedWithLog() = LoggableFieldVisitor(this)

fun FieldVisitor.wrappedWithCheck() = CheckFieldAdapter(this)

fun MethodVisitor.wrappedWithCheck() = CheckMethodAdapter(this)

fun MethodVisitor.wrappedWithLog() = LoggableMethodVisitor(this)

fun MethodVisitor.wrappedWithAdvice(
    access: Int,
    name: String,
    desc: String,
    onEnter: (() -> Unit)? = null,
    onExit: (() -> Unit)? = null
) = run { LoggableAdviceAdapter(this, access, name, desc, onEnter, onExit) }

fun AnnotationVisitor.wrappedWithTrace() = run { TraceAnnotationVisitor(this, Textifier()) }

fun AnnotationVisitor.wrappedWithCheck() = run { CheckAnnotationAdapter(this) }

fun InputStream.applyAsm(func: (cw: ClassVisitor) -> ClassVisitor = { DefaultClassVisitor(it) })
        : ByteArray = Function<InputStream, ByteArray> {

    val cr = ClassReader(this)
    val cw = ClassWriter(cr, ClassWriter.COMPUTE_FRAMES)

    val cv = func.invoke(cw.wrappedWithTrace().wrappedWithLog())

    val parsingOptions = ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES

    cr.accept(cv.wrappedWithTrace().wrappedWithLog(), 0)

    cw.toByteArray()
}.apply(this)
