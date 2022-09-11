package me.yangxiaobin.graph


data class DiEdgeImpl<V>(override val front: V, override val end: V) : DiEdge<V>

open class DiGraphImpl<D, V : DiVertex<D>, E : DiEdge<V>>(initialSize: Int = 128) : GraphImpl<V, E>(initialSize), DiGraph<V, E> {

    override fun inAdjVertexes(vertex: V): Iterable<V> = adjacentVertexes(vertex).filter { it.pointers.contains(vertex) }

    override fun outAdjVertexes(vertex: V): Iterable<V> = adjacentVertexes(vertex).filter { vertex.pointers.contains(it) }
}