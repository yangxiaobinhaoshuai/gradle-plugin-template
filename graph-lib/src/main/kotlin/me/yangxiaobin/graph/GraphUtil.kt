package me.yangxiaobin.graph


/**
 * 计算 v 的度数
 */
fun <V, E> Graph<V, E>.degree(v: V): Int = this.adjacentVertexes(v).count()


/**
 * 计算所有顶点的最大度数
 */
fun <V, E> Graph<V, E>.maxDegree(v: V): Int = this.maxOf { this.degree(it) }


/**
 * 计算所有顶点的平均度数
 */
fun <V, E> Graph<V, E>.averageDegree(): Int = this.e() * 2 / this.v()


/**
 * 计算自环的个数
 */
fun <V, E> Graph<V, E>.selfLoopCount(): Int = 0

/**
 * 图的邻接表表示
 */
fun <V,E> Graph<V,E>.toAdjString():String = ""



