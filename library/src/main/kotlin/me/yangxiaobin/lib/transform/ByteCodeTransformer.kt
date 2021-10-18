package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.asm.api.applyAsm

class DefaultByteCodeTransformer : java.util.function.Function<ByteArray, ByteArray> {

    override fun apply(t: ByteArray): ByteArray {
        return t.applyAsm()
    }
}



