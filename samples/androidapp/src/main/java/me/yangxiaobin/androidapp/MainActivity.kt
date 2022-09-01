package me.yangxiaobin.androidapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import me.yangxiaobin.androidapp.fragments.LogFragment

internal const val LOG_TAG = "ACL-App"

internal fun aLogD(message: String) = android.util.Log.d(LOG_TAG, "$message.")

internal fun aLogE(message: String) = android.util.Log.e(LOG_TAG, "$message.")

class MainActivity : AppCompatActivity() {

    private val rv by lazy { rv_main_activity }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRv()
    }

    private fun setupRv() {
        val fakeList = mutableListOf<Int>()
        for (i in 1..10) fakeList += i
        rv.adapter = RvAdapter(fakeList)
        rv.layoutManager = LinearLayoutManager(this)
        rv.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
    }
}

class RvVh<T>(v: View) : ViewHolder(v) {
    fun bind(t: T, pos: Int) {
        val context = this.itemView.context
        this.itemView.findViewById<TextView>(android.R.id.text1)?.text = t.toString()
        this.itemView.setOnClickListener {
            if (context !is AppCompatActivity) return@setOnClickListener

            when (pos) {

                0 -> {
                    context.supportFragmentManager.commit {
                        add(R.id.root_main_activity, LogFragment())
                        addToBackStack(null)
                    }
                }

                else -> Unit
            }
        }
    }
}

class RvAdapter<T>(private val dataList: List<T>) : Adapter<RvVh<T>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvVh<T> {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        return RvVh(v)
    }

    override fun onBindViewHolder(holder: RvVh<T>, position: Int) {
        holder.bind(dataList[position], position)
    }

    override fun getItemCount(): Int = dataList.size

}

