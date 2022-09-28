package me.yangxiaobin.graph

import java.lang.StringBuilder


/**
 * 计算 v 的度数
 */
fun <V, E> Graph<V, E>.degree(v: V): Int = this.getAdjacentVertexes(v).count()


/**
 * 计算所有顶点的最大度数
 */
fun <V, E> Graph<V, E>.maxDegree(v: V): Int = this.maxOf { this.degree(it) }


/**
 * 计算所有顶点的平均度数
 */
fun <V, E> Graph<V, E>.averageDegree(): Int = this.edgeCount() * 2 / this.vertexCount()


/**
 * 计算自环的个数
 *
 * 邻接顶点集合中仍有自己
 */
fun <V, E> Graph<V, E>.selfLoopCount(): Int {
    var selfCount = 0
    this.iterator().forEach { v ->
        selfCount += this.getAdjacentVertexes(v).filter { it == v }.size
    }
    return selfCount
}

/**
 * 图的邻接表表示
 */
fun <V, E> Graph<V, E>.toAdjString(): String {
    val sb = StringBuilder()

    this.forEach { v ->
        sb.append("\r\nv: $v: \r\n")
        this.getAdjacentVertexes(v).forEach { sb.append("  $it,") }
    }

    return sb.toString()
}



