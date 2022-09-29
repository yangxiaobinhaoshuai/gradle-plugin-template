package me.yangxiaobin.graph.core

import java.util.concurrent.ConcurrentHashMap


interface Vertex<DATA> {

    val data: DATA

    operator fun plus(other: Vertex<DATA>): Edge<Vertex<DATA>>

    override fun hashCode(): Int

    override fun equals(other: Any?): Boolean
}

interface Edge<VERTEX> {

    val first: VERTEX

    val second: VERTEX

    override fun hashCode(): Int

    override fun equals(other: Any?): Boolean
    operator fun component1(): VERTEX = first
    operator fun component2(): VERTEX = second
}

interface Graph<VERTEX, EDGE> : Iterable<VERTEX> {
    /**
     *顶点数
     */
    fun vertexCount(): Int

    /**
     * 边数
     */
    fun edgeCount(): Int

    /**
     * 添加一条边
     * O(1)
     */
    fun addEdge(edge: EDGE)

    /**
     * 删除一个顶点
     * O(v+e)
     */
    fun removeVertex(vertex: VERTEX)

    /**
     * 某个顶点的邻接顶点集合
     * O(v)
     */
    fun getAdjacentVertexes(vertex: VERTEX): Iterable<VERTEX>
}


/**
 * Multi-Thread-Safe
 *
 * 允许单个自环
 *
 * 不允许平行边
 */
open class GraphImpl<V, E : Edge<V>>(initialSize: Int = 128) : Graph<V, E> {

    /**
     * V to LinkedHashSet
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected val adjacencyMap: ConcurrentHashMap<V, MutableSet<V>> = ConcurrentHashMap(initialSize)

    override fun addEdge(edge: E) {

        val firstValue: MutableSet<V> = adjacencyMap.getOrPut(edge.first,::mutableSetOf)
        firstValue.add(edge.second)

        val secondValue: MutableSet<V> = adjacencyMap.getOrPut(edge.second,::mutableSetOf)
        secondValue.add(edge.first)
    }

    override fun getAdjacentVertexes(vertex: V): Iterable<V> = adjacencyMap.getOrDefault(vertex, mutableSetOf())

    override fun removeVertex(vertex: V) {
        if (vertex == null) return

        adjacencyMap.remove(vertex)

        adjacencyMap.forEach { (t: V, u: MutableSet<V>) ->
            adjacencyMap[t] = u.filterNot { it == vertex }.toMutableSet()
        }

    }

    override fun vertexCount(): Int = adjacencyMap.keys.size

    override fun edgeCount(): Int = adjacencyMap.values.flatten().size / 2

    override fun iterator(): Iterator<V> = adjacencyMap.keys().iterator()
}

