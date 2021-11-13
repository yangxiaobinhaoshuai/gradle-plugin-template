package me.yangxiaobin.sample.annotation_process

import me.yangxiaobin.annotation.CustomAnnotation

class Foo {

    @CustomAnnotation
    fun foo() {
        println("----> this is foo function.")
    }

}
