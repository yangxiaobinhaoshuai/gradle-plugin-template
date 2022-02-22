package me.yangxiaobin.android.codelab.recyclerview

import android.graphics.Canvas
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import me.yangxiaobin.android.codelab.R
import me.yangxiaobin.android.kotlin.codelab.base.AbsFragment
import me.yangxiaobin.android.kotlin.codelab.ext.getScreenLocation
import me.yangxiaobin.android.kotlin.codelab.recyclerview.AbsVH
import me.yangxiaobin.android.kotlin.codelab.recyclerview.SimpleRvAdapter

class GridRvFragment : AbsFragment() {

    override val AbsFragment.TAG: String get() = "GridRv"

    override val layoutResId: Int
        get() = R.layout.fragment_recyclerview

    override fun afterViewCreated(view: View) {
        super.afterViewCreated(view)

        val mockList = mutableListOf<Int>()
        repeat(30) { mockList += it }

        rv_fragment.layoutManager = GridLayoutManager(requireContext(), 4)
        rv_fragment.adapter = SimpleRvAdapter(
            mockList,
            android.R.layout.simple_list_item_1
        ) { (vh: AbsVH, pos, _) ->
            vh.requireView<TextView>(android.R.id.text1).run {
                gravity = Gravity.CENTER
                text = mockList[pos].toString()
            }

        }

        val helperCallback = object : ItemTouchHelper.Callback() {

            private val fakeView by lazy {
                TextView(requireContext())
                    .apply {
                        val lp = ViewGroup.MarginLayoutParams(200, 100)
                        this.layoutParams = lp
                        this.text = "FAKE_VIEW"
                        this.updatePadding(20, 20, 20, 20)
                        this.setBackgroundColor(Color.YELLOW)
                        this.setTextColor(Color.BLACK)
                    }
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int = makeMovementFlags(UP or DOWN or START or END, 0)

            override fun onMove(
                rv: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val oldPos = viewHolder.bindingAdapterPosition
                val newPos = target.bindingAdapterPosition
                rv.adapter?.notifyItemMoved(oldPos, newPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                fakeView.isVisible = true
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                fakeView.isGone = true
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                val (screenX, screenY) = viewHolder.itemView.getScreenLocation

                if (!fakeView.isAttachedToWindow) {
                    (requireActivity().window.decorView as ViewGroup).addView(fakeView)
                }

                fakeView.translationX = screenX.toFloat()
                fakeView.translationY = screenY.toFloat()

            }
        }

        ItemTouchHelper(helperCallback).attachToRecyclerView(rv_fragment)

    }
}
