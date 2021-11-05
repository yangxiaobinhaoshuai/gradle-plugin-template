package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.asm.api.applyAsm
import java.io.File

open class ClassFileTransformer(private val innerTransformer: java.util.function.Function<File, File>) :
    java.util.function.Function<File, File> {

    protected fun beforeTransform(f: File) {}

    override fun apply(f: File): File = innerTransformer.apply(f)

    protected fun afterTransform(f: File) {}
}

infix fun <T> java.util.function.Function<T, T>.chained(other: java.util.function.Function<T, T>): java.util.function.Function<T, T> =
    java.util.function.Function { other.apply(other.apply(it)) }


class DefaultByteCodeTransformer : java.util.function.Function<ByteArray, ByteArray> {

    override fun apply(t: ByteArray): ByteArray {
        return t.applyAsm()
    }
}



