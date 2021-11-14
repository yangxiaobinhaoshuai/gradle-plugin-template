package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.domain.AbsKey
import me.yangxiaobin.logger.domain.AbsDomainElement

data class EnableLogElement(val enable: Boolean) : AbsDomainElement(EnableLogElement) {

    companion object Key : AbsKey<EnableLogElement>()

}
