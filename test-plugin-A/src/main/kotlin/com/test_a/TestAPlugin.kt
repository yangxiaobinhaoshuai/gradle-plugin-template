package com.test_a

import com.android.build.api.transform.TransformInvocation
import me.yangxiaobin.lib.BasePlugin
import me.yangxiaobin.lib.TransformRegistry
import me.yangxiaobin.lib.ext.requireAppExtension
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.transform.AbsTransformV2
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class TestATransform(logDelegate: LogAware) : AbsTransformV2(logDelegate) {

    override fun transform(transformInvocation: TransformInvocation) {
        logI("TestATransform isIncremental: $isIncremental.")
        super.transform(transformInvocation)
    }

}

class TestAPlugin : BasePlugin() {

    override fun apply(p: Project) {
        super.apply(p)
        logI("Applied Test A Plugin.")

        afterEvaluate {
            this.requireAppExtension.registerTransform(TestATransform(this@TestAPlugin))
            TransformRegistry.register{
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
                        //println("----> Test A Plugin visit class :$name.")
                    }
                }

                cr.accept(cv, ClassReader.EXPAND_FRAMES)
                cw.toByteArray()
            }
        }

    }
}
