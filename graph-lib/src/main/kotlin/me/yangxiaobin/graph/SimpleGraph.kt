package me.yangxiaobin.graph

import me.yangxiaobin.graph.core.Edge
import me.yangxiaobin.graph.core.Vertex
import java.util.concurrent.ConcurrentHashMap


data class SimpleVertex<T>(override val data: T) : Vertex<T> {
    override fun plus(other: Vertex<T>): Edge<Vertex<T>> = SimpleEdge(this, other)

    override fun toString(): String = data.toString()
    override fun equals(other: Any?): Boolean = when (other) {
        null -> false
        !is SimpleVertex<*> -> false
        else -> this.data == other.data
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }
}

fun <T> T.toSimpleVertex(): SimpleVertex<T> = SimpleVertex(this)


data class SimpleEdge<V>(override val first: V, override val second: V) : Edge<V>


interface SimpleGraph<T> : Iterable<T> {
    /**
     *顶点数
     */
    fun vertexCount(): Int


    fun addVertexPair(v1: T, v2: T): SimpleGraph<T>

    /**
     * 删除一个顶点
     * O(v+e)
     */
    fun removeVertex(vertex: T)

    /**
     * 某个顶点的邻接顶点集合
     * O(v)
     */
    fun getAdjacentVertexes(vertex: T): Iterable<T>
}

/**
 * 1. 不允许自环
 * 2. 不允许平行边
 */
open class SimpleGraphImpl<T>(initialSize: Int = 128) : SimpleGraph<T> {

    protected open val adjacencyMap: MutableMap<T, Collection<T>> = ConcurrentHashMap(initialSize)
    override fun vertexCount(): Int = adjacencyMap.count()

    override fun getAdjacentVertexes(vertex: T): Iterable<T> = adjacencyMap.getOrDefault(vertex, mutableSetOf())

    override fun removeVertex(vertex: T) {
        adjacencyMap.remove(vertex)

        adjacencyMap.forEach { (t, u) ->
            adjacencyMap[t] = u.filterNot { it == vertex }
        }
    }

    override fun addVertexPair(v1: T, v2: T): SimpleGraph<T> {
        val oldV1List = adjacencyMap.getOrDefault(v1, emptyList())
        adjacencyMap[v1] = oldV1List + v2

        val oldV2List = adjacencyMap.getOrDefault(v2, emptyList())
        adjacencyMap[v2] = oldV2List + v1
        return this
    }

    override fun iterator(): Iterator<T> = adjacencyMap.keys.iterator()
}
