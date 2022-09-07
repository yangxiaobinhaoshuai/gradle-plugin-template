package me.yangxiaobin.androidapp

import android.app.Application
import org.aspectj.lang.Aspects

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Aspects.hasAspect(String::class.java)
    }

}
