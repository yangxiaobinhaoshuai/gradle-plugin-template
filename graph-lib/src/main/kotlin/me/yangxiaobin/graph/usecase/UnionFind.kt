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
     * 从 1 开始
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
        val p1 = find(t1)
        val p2 = find(t2)

        if (p1 >= 0 && p2 >= 0 && p1 == p2) return

        val t1Index = idMap.getOrPut(t1) { count.incrementAndGet() }
        idMap[t2] = t1Index
    }

    override fun find(t: T): Int = idMap.getOrDefault(t, -1)

    override fun isConnected(t1: T, t2: T): Boolean {

        val p1 = find(t1)
        val p2 = find(t2)

        return when {
            p1 == -1 || p2 == -1 -> false
            else -> p1 == p2
        }
    }

    override fun count(): Int = count.get()

}


/**
 * 目的为了提高 Union 的效率
 * 与 Quick-Find 算法互补
 * 使用父节点树
 *
 * O(1)
 */
class QuickUnion<T>(mapInitialSize: Int = 64) : UnionFind<T> {
    /**
     * 分量 ids
     */
    private val parentTree: MutableMap<T, T> = ConcurrentHashMap<T, T>(mapInitialSize)

    /**
     * 分量数量
     */
    private val idMap: MutableMap<T, Int> = ConcurrentHashMap<T, Int>()

    private val count = AtomicInteger(0)

    /**
     * t2 的父节点 = t1 的父节点
     *
     * O(1)
     */
    override fun union(t1: T, t2: T) {

        val p1 = find(t1)
        val p2 = find(t2)

        if (p1 >= 0 && p2 >= 0 && p1 == p2) return

        if (parentTree[t1] == null) {
            idMap.computeIfAbsent(t1) { count.getAndIncrement() }
        }

        parentTree[t2] = t1
    }

    override fun find(t: T): Int {

        var parent = parentTree[t]
        while (parent != null && parentTree[parent] != null && parent != t) {
            parent = parentTree[parent]
        }

        return parent?.let { idMap.getOrDefault(it, -1) } ?: -1
    }

    override fun isConnected(t1: T, t2: T): Boolean {
        val p1 = find(t1)
        val p2 = find(t2)
        return when {
            p1 == -1 || p2 == -1 -> false
            else -> p1 == p2
        }
    }

    override fun count(): Int = count.get()
}

/**
 * Base on quick-union
 *
 * O(logn)
 */
class WeightedQuickUnion<T>(mapInitialSize: Int = 64) : UnionFind<T> {

    private val parentTree: MutableMap<T, T> = ConcurrentHashMap<T, T>(mapInitialSize)

    private val idMap: MutableMap<T, Int> = ConcurrentHashMap<T, Int>()

    private val count = AtomicInteger()

    /**
     * 各个连通分量的大小
     */
    private val sizeMap: MutableMap<Int, Int> = ConcurrentHashMap<Int, Int>()

    override fun union(t1: T, t2: T) {

        if (parentTree[t1] == null) {
            idMap.computeIfAbsent(t1) { count.getAndIncrement() }
        }

        val t1Index = find(t1)

        if (parentTree[t2] == null) {
            parentTree[t2] = t1
            sizeMap[t1Index] = (sizeMap[t1Index] ?: 0) + 1
        } else {

            val t2Index = find(t2)

            // t1  >  t2
            if (sizeMap.getOrDefault(t1Index, 0) > sizeMap.getOrDefault(t2Index, 0)) {

                var t1Parent = parentTree[t1]
                while (t1Parent != null && parentTree[t1Parent] != null) t1Parent = parentTree[t1Parent]

                var t2Parent = parentTree[t2]
                while (t2Parent != null && parentTree[t2Parent] != null) t2Parent = parentTree[t2Parent]

                parentTree[t1Parent!!] = t2Parent!!

                sizeMap[t2Index] = (sizeMap[t2Index] ?: 0) + 1
                count.decrementAndGet()
            } else {


                var t1Parent = parentTree[t1]
                while (t1Parent != null && parentTree[t1Parent] != null) t1Parent = parentTree[t1Parent]

                var t2Parent = parentTree[t2]
                while (t2Parent != null && parentTree[t2Parent] != null) t2Parent = parentTree[t2Parent]

                parentTree[t2Parent!!] = t1Parent!!

                sizeMap[t1Index] = (sizeMap[t1Index] ?: 0) + 1
                count.decrementAndGet()
            }
        }
    }

    override fun find(t: T): Int {

        var parent = parentTree[t]
        while (parent != null && parentTree[parent] != null && parent != t) {
            parent = parentTree[parent]
        }

        return parent?.let { idMap.getOrDefault(it, -1) } ?: -1
    }

    override fun isConnected(t1: T, t2: T): Boolean {
        val p1 = find(t1)
        val p2 = find(t2)
        return when {
            p1 == -1 || p2 == -1 -> false
            else -> p1 == p2
        }
    }

    override fun count(): Int = count.get()

}
