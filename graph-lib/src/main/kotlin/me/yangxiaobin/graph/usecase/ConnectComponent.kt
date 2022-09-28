package me.yangxiaobin.graph.usecase

import me.yangxiaobin.graph.Graph

/**
 * 连通分量
 *
 * DFS
 */
class ConnectComponent<V, E>(graph: Graph<V, E>) {

    private val marked = mutableMapOf<V, Boolean>()

    private val ids = mutableMapOf<V, Int>()

    private var count = 0

    init {
        for (v: V in graph) {
            if (marked[v] != true) {
                dfs(graph, v)
                count++
            }
        }
    }

    private fun dfs(graph: Graph<V, E>, target: V) {
        marked[target] = true
        ids[target] = count
        for (v in graph.getAdjacentVertexes(target)) {
            if (marked[v] != true) dfs(graph, v)
        }
    }

    fun isConnected(v1: V, v2: V): Boolean = ids[v1] == ids[v2]


    fun id(v: V): Int = ids.getOrDefault(v, -1)

    fun count(): Int = count

}
