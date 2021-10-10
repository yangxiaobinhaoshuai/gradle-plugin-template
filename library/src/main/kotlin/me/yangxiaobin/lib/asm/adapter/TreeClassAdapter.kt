package me.yangxiaobin.lib.asm.adapter

import org.gradle.api.Action
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.ClassNode


class TreeClassAdapter(
    api: Int,
    private val paramCv: ClassVisitor,
    private val action: Action<ClassNode> = Action {}
) : ClassVisitor(api, ClassNode()) {

    override fun visitEnd() {
        super.visitEnd()
        val cn = this.cv as ClassNode
        action.execute(cn)
        cn.accept(paramCv)
    }
}
