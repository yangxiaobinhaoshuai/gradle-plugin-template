package me.yangxiaobin.androidapp

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.yangxiaobin.androidapp.fragmengs.JavaFragment
import me.yangxiaobin.androidapp.fragmengs.KotlinFragment

class MainActivity : AppCompatActivity() {

    private var mDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        bt1_android_app_main.setOnClickListener {
//            val myDialog = Dialog(this)
//            myDialog.setContentView(android.R.layout.select_dialog_item)
//            myDialog.show()

//            mDialog = Dialog(this)
//            mDialog?.setContentView(android.R.layout.select_dialog_item)
//            mDialog?.show()
//            Instrumentation.recordDialog(myDialog).show()

            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_main_activity, JavaFragment())
                .addToBackStack(null)
                .commit()
        }


        bt2_android_app_main.setOnClickListener {
//            val myDialog = Dialog(this)
//            myDialog.setContentView(android.R.layout.select_dialog_item)
//            myDialog.show()

//            mDialog = Dialog(this)
//            mDialog?.setContentView(android.R.layout.select_dialog_item)
//            mDialog?.show()
//            Instrumentation.recordDialog(myDialog).show()

            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_main_activity, KotlinFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
