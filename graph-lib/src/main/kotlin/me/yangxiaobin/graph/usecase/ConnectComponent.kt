package me.yangxiaobin.graph.usecase

import me.yangxiaobin.graph.SimpleGraph

/**
 * 连通分量
 *
 * DFS
 */
class ConnectComponent<T>(graph: SimpleGraph<T>) {

    private val marked = mutableMapOf<T, Boolean>()

    private val ids = mutableMapOf<T, Int>()

    private var count = 0

    init {
        for (v: T in graph) {
            if (marked[v] != true) {
                dfs(graph, v)
                count++
            }
        }
    }

    private fun dfs(graph: SimpleGraph<T>, target: T) {
        marked[target] = true
        ids[target] = count
        for (v in graph.getAdjacentVertexes(target)) {
            if (marked[v] != true) dfs(graph, v)
        }
    }

    fun isConnected(v1: T, v2: T): Boolean = ids[v1] == ids[v2]


    fun id(v: T): Int = ids.getOrDefault(v, -1)

    fun count(): Int = count

}
