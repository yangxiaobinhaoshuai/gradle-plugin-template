package me.yangxiaobin.graph


/**
 * 计算 v 的度数
 */
fun <V, E> Graph<V, E>.degree(v: V): Int = this.adjacentVertexes(v).count()


/**
 * 计算所有顶点的最大度数
 */
//fun <V, E> Graph<V, E>.maxDegree(v: V): Int = this.vertexes().maxOf { this.degree(it) }







