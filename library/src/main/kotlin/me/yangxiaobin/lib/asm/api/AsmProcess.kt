package me.yangxiaobin.lib.asm.api

import me.yangxiaobin.lib.asm.adapter.TreeClassAdapter
import me.yangxiaobin.lib.asm.log.LoggableAdviceAdapter
import me.yangxiaobin.lib.asm.log.LoggableClassVisitor
import me.yangxiaobin.lib.asm.log.LoggableFieldVisitor
import me.yangxiaobin.lib.asm.log.LoggableMethodVisitor
import me.yangxiaobin.lib.constant.ASM_API
import org.objectweb.asm.*
import org.objectweb.asm.util.*
import java.io.InputStream
import java.io.PrintWriter
import java.util.function.Function


//region ClassVisitor ext
fun ClassVisitor.wrappedWithTrace() = TraceClassVisitor(this, PrintWriter(System.out))

fun ClassVisitor.wrappedWithCheck() = CheckClassAdapter(this)

fun ClassVisitor.wrappedWithLog(api: Int = ASM_API) = LoggableClassVisitor(api, this)
//endregion


//region MethodVisitor ext
fun MethodVisitor.wrappedWithTrace(printer: Printer = Textifier()) = TraceMethodVisitor(this, printer)

fun MethodVisitor.wrappedWithCheck() = CheckMethodAdapter(this)

fun MethodVisitor.wrappedWithLog(api: Int = ASM_API) = LoggableMethodVisitor(api, this)

fun MethodVisitor.wrappedWithAdvice(
    api: Int = ASM_API,
    access: Int,
    name: String,
    desc: String,
    onEnter: (() -> Unit)? = null,
    onExit: (() -> Unit)? = null
) = run { LoggableAdviceAdapter(api, this, access, name, desc, onEnter, onExit) }
//endregion

//region FieldVisitor ext
fun FieldVisitor.wrappedWithTrace(printer: Printer = Textifier()) = TraceFieldVisitor(this, printer)

fun FieldVisitor.wrappedWithLog(api: Int = ASM_API) = LoggableFieldVisitor(api, this)

fun FieldVisitor.wrappedWithCheck() = CheckFieldAdapter(this)
//endregion

//region AnnotationVisitor ext
fun AnnotationVisitor.wrappedWithTrace() = run { TraceAnnotationVisitor(this, Textifier()) }

fun AnnotationVisitor.wrappedWithCheck() = run { CheckAnnotationAdapter(this) }
//endregion

fun InputStream.applyAsm(
    func: (cw: ClassVisitor) -> ClassVisitor = { DefaultClassVisitor(ASM_API, it) }
): ByteArray = Function<InputStream, ByteArray> {

    val cr = ClassReader(this)
    val cw = ClassWriter(cr, ClassWriter.COMPUTE_FRAMES)

    val cv = func.invoke(cw.wrappedWithCheck().wrappedWithTrace().wrappedWithLog())

    val actualCv = TreeClassAdapter(ASM_API, cv)

    val parsingOptions = ClassReader.SKIP_DEBUG or ClassReader.EXPAND_FRAMES

    cr.accept(actualCv.wrappedWithCheck().wrappedWithTrace().wrappedWithLog(), parsingOptions)

    cw.toByteArray()
}.apply(this)
