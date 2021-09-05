package me.yangxiaobin.base_lib


interface ILog {

    fun isEnable(enable: Boolean)

    fun tag(tag: String)

    fun setLevel(level: Level)

    fun v(message: String)

    fun i(message: String)

    fun d(message: String)

    fun e(message: String)

}

enum class Level { VERBOSE, INFO, DEBUG, ERROR }

private class ILogImpl : ILog {


    private var level = Level.DEBUG
    private var enable: Boolean = true

    private var TAG = ""

    override fun isEnable(enable: Boolean) {
        this.enable = enable
    }

    override fun tag(tag: String) {
        this.TAG = tag
    }

    override fun setLevel(level: Level) {
        this.level = level
    }

    override fun v(message: String) {
        if (level <= Level.VERBOSE) System.out.println(message)
    }

    override fun i(message: String) {
        if (level <= Level.INFO) System.out.println(message)
    }

    override fun d(message: String) {
        if (level <= Level.DEBUG) System.out.println(message)
    }

    override fun e(message: String) {
        if (level <= Level.ERROR) System.out.println(message)
    }

}

object Logger : ILog by ILogImpl()