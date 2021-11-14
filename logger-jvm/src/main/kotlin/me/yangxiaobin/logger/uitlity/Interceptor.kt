package me.yangxiaobin.logger.uitlity

import me.yangxiaobin.logger.domain.DomainElement

interface Interceptor {

    val intercept: Boolean

    fun transform(element: DomainElement?): DomainElement?
}
