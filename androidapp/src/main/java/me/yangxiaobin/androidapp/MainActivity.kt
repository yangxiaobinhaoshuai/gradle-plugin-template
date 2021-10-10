package me.yangxiaobin.androidapp

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        bt_android_app_main.setOnClickListener {
            val myDialog = Dialog(this)
            myDialog.setContentView(android.R.layout.select_dialog_item)
            myDialog.show()
        }
    }
}
