package me.yangxiaobin.lib.asm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.io.InputStream
import java.util.function.Function


fun InputStream.applyAsm(func: (cw: ClassVisitor) -> ClassVisitor): ByteArray = Function<InputStream, ByteArray> {

    val cr = ClassReader(this)
    val cw = ClassWriter(cr, ClassWriter.COMPUTE_FRAMES)

    cr.accept(func.invoke(cw), ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)

    cw.toByteArray()
}.apply(this)
