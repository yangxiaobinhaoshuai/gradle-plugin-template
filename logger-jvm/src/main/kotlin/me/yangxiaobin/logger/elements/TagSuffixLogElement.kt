package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.AbsKey
import me.yangxiaobin.logger.AbsLogElement

data class TagSuffixLogElement(val tagSuffix: String) : AbsLogElement(TagSuffixLogElement) {

    companion object Key : AbsKey<TagSuffixLogElement>()
}
