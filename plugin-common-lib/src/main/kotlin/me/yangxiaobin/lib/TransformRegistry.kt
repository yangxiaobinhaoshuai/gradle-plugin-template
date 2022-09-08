package me.yangxiaobin.lib

import me.yangxiaobin.lib.transform.ByteArrayConverter
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

object TransformRegistry {

    private val byteConverters: MutableList<ByteArrayConverter> = mutableListOf()

    init {
        register {
            val cr = ClassReader(it)

            val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)

            val cv = object : ClassVisitor(Opcodes.ASM7, cw) {

                override fun visit(
                    version: Int,
                    access: Int,
                    name: String?,
                    signature: String?,
                    superName: String?,
                    interfaces: Array<out String>?
                ) {
                    super.visit(version, access, name, signature, superName, interfaces)
                    //println("----> default visit class :$name.")
                }
            }

            cr.accept(cv, ClassReader.EXPAND_FRAMES)
            cw.toByteArray()
        }
    }

    /**
     * You'd better invoke this in afterEvaluate
     */
    public fun register(converter: ByteArrayConverter) {
        byteConverters += converter
    }

    fun getFoldConverter(): ByteArrayConverter = ByteArrayConverter { arr ->
        byteConverters.fold(arr) { acc: ByteArray, func: ByteArrayConverter -> func.transform(acc) }
    }

}
