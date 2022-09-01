package me.yangxiaobin.sample

import java.io.File

class MainDemo {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            println("----> create new file , ${System.getProperty("user.dir")}")
            File("/Users/yangxiaobin/Downloads/abc.log").createNewFile()
        }
    }
}
