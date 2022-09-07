package me.yangxiaobin.lib.transform

import me.yangxiaobin.lib.ext.touch
import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.log.LogDelegate
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File

private const val LOG_TAG = "Transformer"
private val defaultLogDelegate = LogDelegate(InternalLogger, LOG_TAG)

/**
 * 全量编译
 *
 * dirinput: /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/build/tmp/kotlin-classes/debug
 * file:     /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/build/tmp/kotlin-classes/debug/me/yangxiaobin/androidapp/MainActivityKt
 * .class
 *
 * out:      /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/build/intermediates/transforms/AbsLegacyTransform/debug/2
 * res:      /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/build/intermediates/transforms/AbsLegacyTransform/debug/2/me/yangxiaobin/androidapp/MainActivityKt.class
 *
 *
 * Empty class
 *          /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/build/intermediates/transforms/TestTransformV2/debug/1/me/yangxiaobin/androidapp/aspect/JavaAspect.class
 */
class ClassFileTransformer(private val logDelegate: LogAware = defaultLogDelegate) : FileTransformer,
    LogAware by logDelegate {

    override fun transform(input: File, out: File): File {

        logI("class transformed from: $input into: $out.")

        val cr = ClassReader(input.readBytes())

        val cv = object : ClassVisitor(Opcodes.ASM7) {

            override fun visit(
                version: Int,
                access: Int,
                name: String?,
                signature: String?,
                superName: String?,
                interfaces: Array<out String>?
            ) {
                super.visit(version, access, name, signature, superName, interfaces)
            }
        }

        val cw = ClassWriter(0)
        cr.accept(cw, 0)
        val bs = cw.toByteArray()
        out.touch().writeBytes(bs)
        return out
    }

}

class JarFileTransformer(private val logDelegate: LogAware = defaultLogDelegate) : FileTransformer,
    LogAware by logDelegate {

    override fun transform(input: File, out: File): File {
        TODO("Not yet implemented")
    }

}

class FileCopyTransformer(private val logDelegate: LogAware = defaultLogDelegate) : FileTransformer,
    LogAware by logDelegate {

    override fun transform(input: File, out: File): File = input.copyTo(out).also { logI("copy from: $input into: $out.") }

}
