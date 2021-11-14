package me.yangxiaobin.logger.uitlity

interface Formatter {

    fun format(tag2Message: Pair<String, String>): Pair<String, String>

}
