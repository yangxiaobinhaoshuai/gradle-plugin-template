package me.yangxiaobin.lib.asm.api

import me.yangxiaobin.lib.asm.adapter.TreeClassAdapter
import me.yangxiaobin.lib.asm.adapter.TreeMethodAdapter
import me.yangxiaobin.lib.asm.constant.ASM_API
import me.yangxiaobin.lib.asm.log.*
import org.gradle.api.Action
import org.objectweb.asm.*
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.util.*
import java.io.InputStream
import java.io.PrintWriter


//region ClassVisitor ext
fun ClassVisitor.wrappedWithTrace() = TraceClassVisitor(this, PrintWriter(System.out))

fun ClassVisitor.wrappedWithCheck() = CheckClassAdapter(this)

fun ClassVisitor.wrappedWithLog(api: Int = ASM_API) = LoggableClassVisitor(api, this)

fun ClassVisitor.wrappedWithTreeAdapter(api: Int = ASM_API, transform: Action<ClassNode>) =
    TreeClassAdapter(api, this, transform)
//endregion


//region MethodVisitor ext

internal val innerTextifier = InternalTextifier(ASM_API)
internal val innerASMifier = InternalASMifier(ASM_API)

fun MethodVisitor.wrappedWithTrace(printer: Printer = innerTextifier) = TraceMethodVisitor(this, printer)

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

fun MethodVisitor.wrappedWithTreeAdapter(
    api: Int,
    access: Int,
    name: String,
    desc: String,
    signature: String?,
    exceptions: Array<String>?,
    action: Action<MethodNode> = Action {}
) = TreeMethodAdapter(api, access, name, desc, signature, exceptions, this, action)
//endregion

//region FieldVisitor ext
fun FieldVisitor.wrappedWithTrace(printer: Printer = innerTextifier) = TraceFieldVisitor(this, printer)

fun FieldVisitor.wrappedWithLog(api: Int = ASM_API) = LoggableFieldVisitor(api, this)

fun FieldVisitor.wrappedWithCheck() = CheckFieldAdapter(this)
//endregion

//region AnnotationVisitor ext
fun AnnotationVisitor.wrappedWithTrace() = TraceAnnotationVisitor(this, innerTextifier)

fun AnnotationVisitor.wrappedWithCheck() = CheckAnnotationAdapter(this)
//endregion

// region asm api
fun InputStream.applyAsm(
    writeFlag: Int = ClassWriter.COMPUTE_FRAMES,
    parsingOptions: Int = ClassReader.EXPAND_FRAMES,
    wrap: (cw: ClassVisitor) -> ClassVisitor = { DefaultClassVisitor(ASM_API, it) },
): ByteArray = run {

    //Logger.setLevel(LogLevel.VERBOSE)

    val cr = ClassReader(this)
    val cw = ClassWriter(cr, writeFlag)

    val cv = wrap.invoke(
        cw
        //.wrappedWithCheck()
//            .wrappedWithTrace()
//            .wrappedWithLog()
    )


    cr.accept(
        cv
        //.wrappedWithCheck()
//            .wrappedWithTrace()
//            .wrappedWithLog()
        , parsingOptions
    )

    cw.toByteArray()
}

fun ByteArray.applyAsm(
    writeFlag: Int = ClassWriter.COMPUTE_FRAMES,
    parsingOptions: Int = ClassReader.EXPAND_FRAMES,
    wrap: (cw: ClassVisitor) -> ClassVisitor = { DefaultClassVisitor(ASM_API, it) },
): ByteArray = run {

    val cr = ClassReader(this)
    val cw = ClassWriter(cr, writeFlag)

    val cv = wrap.invoke(
        cw
        //.wrappedWithCheck()
//            .wrappedWithTrace()
//            .wrappedWithLog()
    )


    cr.accept(
        cv
        //.wrappedWithCheck()
//            .wrappedWithTrace()
//            .wrappedWithLog()
        , parsingOptions
    )

    cw.toByteArray()
}
// endregion
