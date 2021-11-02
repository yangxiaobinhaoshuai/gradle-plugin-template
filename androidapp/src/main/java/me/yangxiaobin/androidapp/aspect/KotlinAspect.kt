package me.yangxiaobin.androidapp.aspect

import android.util.Log
import me.yangxiaobin.androidapp.util.logCurried
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect


@Aspect
class KotlinAspect {

    private val TAG = "KotlinAspect"


    val logD = logCurried(TAG)


    // Application aspect
    @After("execution(* android.app.Application.onCreate(..))")
    fun doAfterApplicationOnCreate() {
        logD("After Application OnCreate")
    }

    // Activity aspect
    @After("execution(* android.app.Activity.onCreate(..))")
    fun doAfterActivityOnCreate() {
        logD("After Activity OnCreate")
    }

    @After("execution(* android.app.Activity.onResume())")
    fun doAfterActivityOnResume() {
        logD("After Activity OnResume")
    }

    @After("execution(* android.app.Activity.onDestroy(..))")
    fun doAfterActivityOnDestroy() {
        logD(" After Activity OnDestroy")
    }

    // androidx.fragment.app.Fragment
    // Fragment aspect
    @After("execution(* androidx.fragment.app.Fragment.onCreate(..))")
    fun doAfterFragmentOnCreate() {
        logD(" After Fragment OnCreate")
    }

}
