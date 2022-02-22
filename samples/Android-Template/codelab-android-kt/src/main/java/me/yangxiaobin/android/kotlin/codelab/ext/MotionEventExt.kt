package me.yangxiaobin.android.kotlin.codelab.ext

import android.view.MotionEvent
import android.view.View

// MotionEvent
fun MotionEvent.isOnView(v: View): Boolean {

    val motionRawX = this.rawX
    val motionRawY = this.rawY

    val (viewRawX,viewRawY) = v.getScreenLocation

    return (motionRawX >= viewRawX && motionRawX <= viewRawX + v.width)
            && (motionRawY >= viewRawY && motionRawY <= viewRawY + v.height)
}

val MotionEvent.getActionString: String
    get() = when (this.action) {
        MotionEvent.ACTION_DOWN -> "DOWN"
        MotionEvent.ACTION_MOVE -> "MOVE"
        MotionEvent.ACTION_UP -> "UP"
        MotionEvent.ACTION_CANCEL -> "CANCEL"
        else -> this.action.toString()
    }
