package me.yangxiaobin.graph

import java.util.concurrent.ConcurrentHashMap


data class SimpleVertex<T>(override val data: T) : Vertex<T> {

    override fun plus(other: Vertex<T>): Edge<Vertex<T>> = SimpleEdge(this, other)
}


data class SimpleEdge<V>(override val first: V, override val second: V) : Edge<V>


open class GraphImpl<V, E : Edge<V>>(initialSize: Int = 128) : Graph<V, E> {

    /**
     * V to LinkedHashSet
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected val adjacencyMap: ConcurrentHashMap<V, MutableSet<V>> = ConcurrentHashMap(initialSize)

    override fun addEdge(edge: E) {
        val vertexes: MutableSet<V> = adjacencyMap.getOrPut(edge.first) { mutableSetOf() }
        vertexes.add(edge.second)
    }

    override fun adjacentVertexes(vertex: V): Iterable<V> = adjacencyMap.getOrDefault(vertex, mutableSetOf())

    override fun toAdjString(): String {
        return super.toAdjString()
    }
}
