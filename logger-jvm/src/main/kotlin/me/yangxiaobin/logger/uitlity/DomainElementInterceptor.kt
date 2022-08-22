package me.yangxiaobin.logger.uitlity

import me.yangxiaobin.logger.domain.DomainElement

interface DomainElementInterceptor {

    fun wantIntercept(element: DomainElement?):Boolean

    fun transform(element: DomainElement?): DomainElement?
}
