package me.yangxiaobin.lib

import me.yangxiaobin.lib.ext.neatName
import me.yangxiaobin.lib.ext.requireAppExtension
import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.transform_v3.FunctionInvoker
import me.yangxiaobin.lib.transform_v3.FunctionKey
import me.yangxiaobin.lib.transform_v3.TransformAwareManager
import me.yangxiaobin.lib.transform_v3.TransformDispatcher
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

const val ESC = '\u001B'

const val CSI_RESET = "$ESC[0m"

const val CSI_RED = "$ESC[31m"
fun red(s: Any) = "${CSI_RED}${s}${CSI_RESET}"

class SequenceTransformPlugin : BasePlugin() {

    override val LOG_TAG: String get() = this.neatName

    override val myLogger: ILog get() = super.myLogger.setGlobalPrefix("TB @ ")

    override fun apply(p: Project) {
        super.apply(p)
        logI("${red("✓")} apply TestBasePlugin")


        // TODO
        FunctionInvoker.hook(FunctionKey.of("postTransform")){
            println("_-- hook works")
        }

        // TODO
        TransformAwareManager.registerTransformAware { _, bs ->
            val cr = ClassReader(bs)

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
                    //println("----> register visit class :$name.")
                }
            }

            cr.accept(cv, ClassReader.EXPAND_FRAMES)
            cw.toByteArray()
        }

        //val legacyTransform = AbsLegacyTransform(p)
        val dispatchTransformer = TransformDispatcher(this)

        afterEvaluate {
            TransformAwareManager.registerTransformAware(dispatchTransformer)
            this.requireAppExtension.registerTransform(dispatchTransformer)
        }
    }
}

