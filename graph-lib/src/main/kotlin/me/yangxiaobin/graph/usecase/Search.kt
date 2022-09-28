package me.yangxiaobin.graph.usecase

import me.yangxiaobin.graph.Graph

/**
 * Via DFS
 */
class Search<V, E>(graph: Graph<V, E>, target: V) {

    private val marked = mutableMapOf<V, Boolean>()

    private var count = 0

    init {
        dfs(graph,target)
    }

    /**
     * 两顶点是否连通
     */
    fun isConnected(v: V): Boolean = marked.getOrDefault(v, false)

    private fun dfs(graph: Graph<V, E>, target: V) {
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
