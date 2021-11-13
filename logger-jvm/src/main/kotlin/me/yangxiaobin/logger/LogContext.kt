package me.yangxiaobin.logger

/**
 * An indexed set.
 */
interface LogContext {

    fun <R> fold(initial: R, operation: (R, LogElement) -> R): R

    operator fun <E : LogElement> get(key: Key<E>): E?

    fun minusKey(removedKey: Key<*>): LogContext

    operator fun plus(other: LogContext): LogContext {
        if (other === EmptyLogContext) return this

        return other.fold(this) { acc: LogContext, element: LogElement ->

            val retained: LogContext = acc.minusKey(element.key)

            if (retained === EmptyLogContext) element
            else CombinedLogContext(retained, element)
        }
    }
}

interface LogElement : LogContext {

    val key: Key<*>

    override fun <E : LogElement> get(key: Key<E>): E? =
        @Suppress("UNCHECKED_CAST")
        if (this.key == key) this as E else null

    override fun <R> fold(initial: R, operation: (R, LogElement) -> R): R = operation(initial, this)

    /**
     * Return a [LogContext] without [LogElement] with the specific key.
     */
    override fun minusKey(removedKey: Key<*>): LogContext {
        return if (this.key == removedKey) EmptyLogContext else this
    }
}


interface Key<E : LogElement>

internal class CombinedLogContext(
    private val left: LogContext,
    private val right: LogElement
) : LogContext {

    override fun <E : LogElement> get(key: Key<E>): E? {
        return right[key] ?: left[key]
    }

    override fun minusKey(removedKey: Key<*>): LogContext {
        val leftRetained by lazy { left.minusKey(removedKey) }
        return when {
            right[removedKey] != null -> left
            leftRetained === left -> this
            leftRetained === EmptyLogContext -> right
            else -> CombinedLogContext(leftRetained, right)
        }
    }

    override fun <R> fold(initial: R, operation: (R, LogElement) -> R): R =
        operation(left.fold(initial, operation), right)

}

object EmptyLogContext : LogContext {

    override fun <R> fold(initial: R, operation: (R, LogElement) -> R): R = initial

    override fun <E : LogElement> get(key: Key<E>): E? = null

    override fun minusKey(removedKey: Key<*>): LogContext = this

    override fun plus(other: LogContext): LogContext = other
}

abstract class AbsLogElement(override val key: Key<*>) : LogElement

abstract class AbsKey<E : LogElement> : Key<E>


