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
open class GridSpaceDivider() : RecyclerView.ItemDecoration() {

    private var mSpanCount = 0//列表显示的列数

    private var avgGap = 0
    private var marginTop = 0
    private var onePositionLeft = 0
    private var twoPositionLeft = 0
    private var threePositionLeft = 0
    private var mGm: GridLayoutManager? = null


    private fun initAvgGap(parent: RecyclerView) {
        if (mGm != null) {
            return
        }
        mGm = parent.layoutManager as GridLayoutManager
        mSpanCount = mGm!!.spanCount
        var left = RvDpUtils.dp2px(getLeftGap())
        var right = RvDpUtils.dp2px(getRightGap())
        var middle = RvDpUtils.dp2px(getMiddleGap())
        var top = RvDpUtils.dp2px(getTopGap())

        avgGap = (left + right + middle * 2) / (mGm!!.spanCount)
        marginTop = top
        onePositionLeft = left
        //  if (avgGap > left) {
        // 24-16 =8
        twoPositionLeft = middle + left - avgGap  // 8
        threePositionLeft = avgGap - right

        Ls.d(
            "left=$left right=$right middle=$middle top=$top oneLeft=$onePositionLeft twoLeft=$twoPositionLeft" +
                    " threeLeft=$threePositionLeft  avgGap=$avgGap  spanCount=${mGm!!.spanCount}"
        )
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
        // Ls.d("left getItemOffsets() position=$position spanSize=$spanSize mSpanCount=$mSpanCount")
        if (mSpanCount == spanSize) return

        when (position % mSpanCount) {
            0 -> {
                outRect.set(onePositionLeft, marginTop, 0, 0)
            }
            1 -> {
                outRect.set(twoPositionLeft, marginTop, 0, 0)
            }
            2 -> {
                outRect.set(threePositionLeft, marginTop, 0, 0)
            }
        }
    }

    open fun getTopGap(): Int {
        return 0
    }

    open fun getLeftGap(): Int {
        return 0
    }

    open fun getBottom(): Int {
        return 0
    }

    open fun getRightGap(): Int {
        return 0
    }

    open fun getMiddleGap(): Int {
        return 0
    }
}

