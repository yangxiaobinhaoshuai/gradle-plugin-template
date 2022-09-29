package me.yangxiaobin.graph.usecase

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


/**
 * 解决连通性问题
 * 优势 : 检查两点连通 和 添加一条边有效率
 *
 * 动态算法: 任何时候接近常数时间内，判断两点连通性或者完成大量连通性查询和插入混合操作
 */
interface UnionFind<T> {
    /**
     * 添加一条连接
     */
    fun union(t1: T, t2: T)

    /**
     * 所在连通分量标识符
     */
    fun find(t: T): Int


    /**
     * 判断两点是否处于同一分量
     */
    fun isConnected(t1: T, t2: T): Boolean

    /**
     * 连通分量的总数量
     */
    fun count(): Int
}

class QuickFind<T>(mapInitialSize: Int = 64) : UnionFind<T> {

    /**
     * 分量 ids
     */
    private val idMap: Map<T, Int> = ConcurrentHashMap<T, Int>(mapInitialSize)

    /**
     * 分量数量
     */
    private var count = AtomicInteger(0)
    override fun union(t1: T, t2: T) {
        idMap as ConcurrentHashMap<T, Int>

        val t1Index = idMap.getOrPut(t1) { count.incrementAndGet() }
        idMap[t2] = t1Index
    }

    override fun find(t: T): Int = idMap.getOrDefault(t, -1)

    override fun isConnected(t1: T, t2: T): Boolean = when {
        find(t1) == -1 || find(t2) == -1 -> false
        else -> find(t1) == find(t2)
    }

    override fun count(): Int = count.get()

}


class QuickUnion<T> : UnionFind<T> {
    override fun union(t1: T, t2: T) {
        TODO("Not yet implemented")
    }

    override fun find(t: T): Int {
        TODO("Not yet implemented")
    }

    override fun isConnected(t1: T, t2: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun count(): Int {
        TODO("Not yet implemented")
    }
}

class Weighted
