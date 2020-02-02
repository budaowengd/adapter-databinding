package me.lx.sample.divider

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.tools.Ls

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/10/18 17:27
 *  version: 1.0
 *  desc: @link[androidx.recyclerview.widget.DividerItemDecoration]
 * 垂直列表分割线,可以设置每个item离上1个item的间距
 */
open class GridSpaceDividerSupportMatch222() :
    RecyclerView.ItemDecoration() {

    private var mSpanCount = 0//列表显示的列数

    private var x1 = 0
    private var x2 = 0
    private var y12 = 0
    private var marginTop = 0
    private var mGm: GridLayoutManager? = null


    private fun initAvgGap(parent: RecyclerView) {
        if (mGm != null) {
            return
        }
        mGm = parent.layoutManager as GridLayoutManager
        mSpanCount = mGm!!.spanCount
        x1 = RvDpUtils.dp2px(getX1())
        x2 = RvDpUtils.dp2px(getX2())
        y12 = RvDpUtils.dp2px(getY1())
        marginTop = RvDpUtils.dp2px(getTopGap())
    }


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        initAvgGap(parent)
        val position = parent.getChildAdapterPosition(view)
        val spanSize: Int = mGm!!.spanSizeLookup.getSpanSize(position)
        Ls.d(
            "left getItemOffsets() position=$position spanSize=$spanSize mSpanCount=$mSpanCount  " +
                    "vH=${view.measuredHeight}  vH2=${view.height} vW=${view.width}"
        )
        if (mSpanCount == spanSize) return
        when (position % mSpanCount) {
            0 -> {
                outRect.set(x1, marginTop, x2, 0)
            }
            1 -> {
                outRect.set(y12, marginTop, y12, 0)
            }
            2 -> {
                outRect.set(x2, marginTop, x1, 0)
            }
        }
    }

    open fun getAvgGap(): Int {
        return 0
    }

    open fun getTopGap(): Int {
        return 0
    }

    private fun getX1(): Int {
        return getAvgGap()
    }

    private fun getX2(): Int {
        return getY1() * 2 - getX1()
    }

    private fun getY1(): Int {
        return getAvgGap() * 2 / 3
    }


}

