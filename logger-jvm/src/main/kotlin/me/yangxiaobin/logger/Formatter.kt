package me.yangxiaobin.logger

interface Formatter {

    fun format(tag2Message: Pair<String, String>): Pair<String, String>

}
