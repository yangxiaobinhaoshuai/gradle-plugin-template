package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.domain.AbsKey
import me.yangxiaobin.logger.domain.AbsDomainElement

data class GlobalTagPrefixLogElement(val tagPrefix: String) : AbsDomainElement(GlobalTagPrefixLogElement) {

    companion object Key : AbsKey<GlobalTagPrefixLogElement>()
}
