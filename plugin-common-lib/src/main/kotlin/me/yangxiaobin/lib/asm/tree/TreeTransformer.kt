@file:Suppress("MemberVisibilityCanBePrivate")

package me.yangxiaobin.lib.asm.tree

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

open class TreeClassTransformer(private val tct: TreeClassTransformer?) {

    fun transform(cn: ClassNode) {
        tct?.transform(cn)
    }
}


open class TreeMethodTransformer(private val tmt: TreeMethodTransformer?) {

    fun transform(mn: MethodNode) {
        tmt?.transform(mn)
    }
}

open class TreeFieldTransformer(private val tft: TreeFieldTransformer?) {

    fun transform(fn: FieldNode) {
        tft?.transform(fn)

    }
}
