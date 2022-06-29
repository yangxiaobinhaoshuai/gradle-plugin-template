package me.yangxiaobin.lib.asm.adapter

import org.gradle.api.Action
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.tree.MethodNode

class TreeMethodAdapter(
    api: Int,
    access: Int,
    name: String,
    desc: String,
    signature: String?,
    exceptions: Array<String>?,
    private val paramMv: MethodVisitor,
    private val action: Action<MethodNode> = Action {}
) : MethodVisitor(api, MethodNode(api, access, name, desc, signature, exceptions)) {


    override fun visitEnd() {
        super.visitEnd()
        val mn = this.mv as MethodNode
        action.execute(mn)
        mn.accept(paramMv)
    }
}
