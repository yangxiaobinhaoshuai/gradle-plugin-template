package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.asm.api.applyAsm

interface ByteCodeTransformer {

    fun transformByteArray(raw: ByteArray):ByteArray
}


class DefaultByteCodeTransformer : ByteCodeTransformer {

    override fun transformByteArray(raw: ByteArray): ByteArray {
        return raw.applyAsm()
    }
}



