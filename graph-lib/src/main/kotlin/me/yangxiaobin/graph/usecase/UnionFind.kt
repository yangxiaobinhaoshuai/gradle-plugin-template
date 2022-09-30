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


/**
 * 特点：
 * find 效率很高, Union 复杂度很高
 *
 * 对于规模为 n 的元素集合，时间复杂度为 ：O(n)
 */
class QuickFind<T>(mapInitialSize: Int = 64) : UnionFind<T> {

    /**
     * 分量 ids
     */
    private val idMap: MutableMap<T, Int> = ConcurrentHashMap<T, Int>(mapInitialSize)

    /**
     * 分量数量
     */
    private var count = AtomicInteger(0)

    /**
     * 归并 t1 和 t2 到同一个分量
     *
     * 将 t2 的 index 改为 t1 的 index
     *
     * O(1)
     */
    override fun union(t1: T, t2: T) {
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


/**
 * 目的为了提高 Union 的效率
 * 与 Quick-Find 算法互补
 * 使用父节点树
 */
class QuickUnion<T>(mapInitialSize: Int = 64) : UnionFind<T> {

    /**
     * 分量 ids
     */
    private val parentTree: MutableMap<T, Pair<T, Int>> = ConcurrentHashMap<T, Pair<T, Int>>(mapInitialSize)


    /**
     * 分量数量
     */
    private var count = AtomicInteger(0)

    /**
     * t2 的父节点 = t1 的父节点
     */
    override fun union(t1: T, t2: T) {

        if (find(t1) > 0 && find(t2) > 0 && find(t1) == find(t2)) return

        val t1Parent: Pair<T, Int> = parentTree.getOrPut(t1) { t1 to count.getAndIncrement() }

        parentTree[t2] = t1Parent
    }

    override fun find(t: T): Int = parentTree[t]?.second ?: -1

    override fun isConnected(t1: T, t2: T): Boolean = when {
        find(t1) == -1 || find(t2) == -1 -> false
        else -> find(t1) == find(t2)
    }

    override fun count(): Int = count.get()
}

class WeightedQuickUnion<T> : UnionFind<T> {
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
