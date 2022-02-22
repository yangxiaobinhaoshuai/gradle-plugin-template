package me.yangxiaobin.android.kotlin.codelab.ext

import android.content.Context
import android.view.LayoutInflater


val Context.inflater: LayoutInflater get() = android.view.LayoutInflater.from(this)
