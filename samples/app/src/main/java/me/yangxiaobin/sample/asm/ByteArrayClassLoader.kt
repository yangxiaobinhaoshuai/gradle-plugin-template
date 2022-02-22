package me.yangxiaobin.sample.asm

class ByteArrayClassLoader(private val byteArr: ByteArray) : ClassLoader() {

    override fun findClass(name: String?): Class<*> {
        println("-----> ByteArrayClassLoader find class, name :$name")
        return defineClass(name, byteArr, 0, byteArr.size)
        //return super.findClass(name)
    }
}
