package me.yangxiaobin.android.kotlin.codelab.base

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import me.yangxiaobin.android.kotlin.codelab.log.L
import me.yangxiaobin.kotlin.codelab.log.logD
import me.yangxiaobin.kotlin.codelab.log.logE
import me.yangxiaobin.kotlin.codelab.log.logI

abstract class AbsActivity : AppCompatActivity() {

    protected open val AbsActivity.TAG: String
        get() = "AbsActivity:${this.javaClass.simpleName.take(11)}"

    protected val logI by lazy { L.logI(TAG) }
    protected val logD by lazy { L.logD(TAG) }
    protected val logE by lazy { L.logE(TAG) }

    protected abstract val contentResId: Int

    open val handleBackPress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentResId)
        logI("${this.hashCode()} onCreate, savedInstanceState :$savedInstanceState")
        registerBackHandler()
        afterOnCreate()

    }

    private fun registerBackHandler() = this.onBackPressedDispatcher.addCallback(this,
        object : OnBackPressedCallback(handleBackPress) {
            override fun handleOnBackPressed() {
                onHandleBackPress()
            }

        })

    protected open fun afterOnCreate() {}

    protected open fun onHandleBackPress() {}

    override fun onStart() {
        super.onStart()
        logI("${this.hashCode()} onStart")
    }

    override fun onRestart() {
        super.onRestart()
        logI("${this.hashCode()} onRestart")
    }

    override fun onResume() {
        super.onResume()
        logI("${this.hashCode()} onResume")
    }

    override fun onPause() {
        super.onPause()
        logI("${this.hashCode()} onPause")
    }

    override fun onStop() {
        super.onStop()
        logI("${this.hashCode()} onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logI("${this.hashCode()} onDestroy")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        logI("${this.hashCode()} onConfigurationChanged, newConfig: $newConfig")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        logI("${this.hashCode()} onNewIntent, intent: $intent")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        logI("${this.hashCode()} onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        logI("${this.hashCode()} onTrimMemory, level :$level")
    }

    override fun finish() {
        super.finish()
        logI("${this.hashCode()} finish")
    }
}
