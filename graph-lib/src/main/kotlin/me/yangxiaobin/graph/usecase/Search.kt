package me.yangxiaobin.graph.usecase

import me.yangxiaobin.graph.SimpleGraph

/**
 * Via DFS
 */
class Search<T>(graph: SimpleGraph<T>, target: T) {

    private val marked = mutableMapOf<T, Boolean>()

    private var count = 0

    init {
        dfs(graph, target)
    }

    /**
     * 两顶点是否连通
     */
    fun isConnected(v: T): Boolean = marked.getOrDefault(v, false)

    private fun dfs(graph: SimpleGraph<T>, target: T) {
        marked[target] = true
        count++

        for (v in graph.getAdjacentVertexes(target)) {
            if (marked[v] != true) dfs(graph, v)
        }
    }

    /**
     * 与某个顶点连通的顶点总数
     * 包含自己
     */
    fun getConnectedCount(): Int = count

}
