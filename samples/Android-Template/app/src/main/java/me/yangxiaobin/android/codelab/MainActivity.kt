package me.yangxiaobin.android.codelab

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import me.yangxiaobin.android.codelab.recyclerview.GridRvFragment
import me.yangxiaobin.android.codelab.recyclerview.LinearRvFragment
import me.yangxiaobin.android.codelab.recyclerview.PagingRvFragment
import me.yangxiaobin.android.kotlin.codelab.base.AbsActivity
import me.yangxiaobin.android.kotlin.codelab.ext.setOnItemClickListener
import me.yangxiaobin.android.kotlin.codelab.recyclerview.SimpleRvAdapter

class MainActivity : AbsActivity() {

    private val dataList = listOf("LinearRv", "GridRv","PagingRv")

    override val AbsActivity.TAG: String get() = "Sample-app"


    override val contentResId: Int
        get() = R.layout.activity_main

    override val handleBackPress: Boolean
        get() = this.isTaskRoot

    override fun onHandleBackPress() {
        super.onHandleBackPress()
        this.moveTaskToBack(true)
    }

    override fun afterOnCreate() {
        super.afterOnCreate()
        init()
    }

    private fun init() {
        val rv = rv_main_activity

        rv.layoutManager = LinearLayoutManager(this)

        rv.adapter =
            SimpleRvAdapter<String>(dataList, android.R.layout.simple_list_item_1) { (vh, pos, _) ->
                vh.requireView<TextView>(android.R.id.text1).text = dataList[pos]
            }

        rv.setOnItemClickListener {
            logI("rv onItem click :$it")

            fun navigateFragment(target: Fragment) = supportFragmentManager.commit {
                addToBackStack(null)
                add(R.id.content_main_activity, target)
            }

            when (it.second) {
                0 -> navigateFragment(LinearRvFragment())
                1 -> navigateFragment(GridRvFragment())
                2 -> navigateFragment(PagingRvFragment())
            }
        }

    }

}
