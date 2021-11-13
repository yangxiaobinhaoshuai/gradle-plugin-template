package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.AbsKey
import me.yangxiaobin.logger.AbsLogElement

data class TagPrefixLogElement(val tagPrefix: String) : AbsLogElement(TagPrefixLogElement) {

    companion object Key : AbsKey<TagPrefixLogElement>()
}
