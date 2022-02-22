package me.yangxiaobin.android.kotlin.codelab.recyclerview

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView
import me.yangxiaobin.android.kotlin.codelab.ext.inflater
import me.yangxiaobin.android.kotlin.codelab.log.L
import me.yangxiaobin.kotlin.codelab.log.logI

open class AbsVH(wholeItemView: View) : RecyclerView.ViewHolder(wholeItemView) {

    private val viewCache = SparseArray<View>(16)

    private val tag = "AbsVH"
    private val logI by lazy { L.logI(tag) }

    @Suppress("UNCHECKED_CAST")
    open fun <T : View?> findView(@IdRes resId: Int): T? = (viewCache[resId] as? T)
        ?: itemView.findViewById<T>(resId)?.also { viewCache[resId] = it }
        ?: run { logI("Can not find associated view with id: $resId");null }

    open fun <T : View> requireView(@IdRes resId: Int): T = this.findView<T>(resId)
        ?: throw NullPointerException("Can NOT find view relative to id: $this")
}

typealias  BindVH <T> = (Triple<T, Int, MutableList<Any>>) -> Unit

class SimpleRvAdapter<T>(
    private val dataList: List<T>,
    private val itemLayoutResId: Int,
    private val binding: BindVH<AbsVH>,
) : RecyclerView.Adapter<AbsVH>() {

    private val tag = "SimpleRvAdapter"
    private val logI by lazy { L.logI(tag) }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsVH {
        val vhItemView = parent.context.inflater.inflate(itemLayoutResId, parent, false)
        logI("onCreateViewHolder")
        return AbsVH(vhItemView)
    }

    override fun onBindViewHolder(holder: AbsVH, position: Int, payloads: MutableList<Any>) {
        logI("onBindViewHolder with payloads")

        if (payloads.isNotEmpty()) binding.invoke(Triple(holder, position, payloads))
        else super.onBindViewHolder(holder, position, payloads)

    }

    override fun onBindViewHolder(holder: AbsVH, position: Int) {
        logI("onBindViewHolder")
        binding.invoke(Triple(holder, position, mutableListOf()))
    }

}
