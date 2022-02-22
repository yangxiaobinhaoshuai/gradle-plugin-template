package me.yangxiaobin.aspectandroid

open class AspectAndroidExt {
    var aspectJrtVersion = "1.9.7"

    /**
     * 是否需要生成 classpath 和 AspectJ weaveInfo 信息文件，用于调试
     */
    var generateDebugLogFile = true


    /**
     *  是否需要织入 dependency jars
     */
    var supportTransitiveJars = true
}
