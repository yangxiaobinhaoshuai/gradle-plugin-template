package me.yangxiaobin.androidapp.aspect

import android.util.Log


fun logCurried(tag: String) = fun(message: String) = Log.d(tag, "===> $message")


