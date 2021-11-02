package me.yangxiaobin.androidapp.util

import android.util.Log


fun logCurried(tag: String) = fun(message: String) = Log.d(tag, "===> $message")


