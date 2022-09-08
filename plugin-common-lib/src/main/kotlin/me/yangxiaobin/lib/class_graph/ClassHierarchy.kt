package me.yangxiaobin.lib.class_graph

import me.yangxiaobin.graph.*
import javax.xml.soap.Node


@Suppress("ArrayInDataClass")
data class ClassInfo(
    /**
     * 文件全路径
     */
    val canonicalName: String,
    /**
     * 父类
     */
    val superClasses: List<String>,
    /**
     * 父接口
     */
    val superInterfaces: List<String>,
)


typealias ClassEdge = DiEdgeImpl<ClassNode>

class ClassNode(
    override val pointers: Iterable<DiVertex<ClassInfo>>,
    override val data: ClassInfo,
) : DiVertex<ClassInfo> {

    override fun plus(other: Vertex<ClassInfo>): Edge<Vertex<ClassInfo>> = DiEdgeImpl(this, other)

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is DiVertex<*>) return false
        return this.data == other.data && this.pointers == other.pointers
    }

}


class ClassHierarchyGraph : DiGraphImpl<ClassInfo, ClassNode, ClassEdge>() {


    fun getDerivedNodes(node: ClassNode): List<ClassNode> {

        return emptyList()
    }

    fun getSuperNodes(node: ClassNode): List<Node> {

        return emptyList()
    }

}
