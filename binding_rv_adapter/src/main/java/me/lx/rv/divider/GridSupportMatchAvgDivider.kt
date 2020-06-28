package me.lx.rv.divider

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.tools.Ls
import me.lx.rv.tools.RvUtils

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/10/18 17:27
 *  version: 1.0
 *  desc: @link[androidx.recyclerview.widget.DividerItemDecoration]
 *如果item布局是marchParent
 * 可以使用该分割线平方每列的间距
 * 只支持2列和3列
 */
open class GridSupportMatchAvgDivider() :
    RecyclerView.ItemDecoration() {
    private var mSpanCount = 0//列表显示的列数

    private var oneLeft = 0
    private var oneRight = 0
    private var middleLeft = 0
    private var marginTop = 0
    private var marginBottom = 0
    private var mAvgGap = 0
    private var mGm: GridLayoutManager? = null

    private var hasHeader = false
    fun setAvgGap(gap: Int): GridSupportMatchAvgDivider {
        mAvgGap = RvUtils.dp2px(gap)
        return this
    }

    fun setHasHeader(): GridSupportMatchAvgDivider {
        hasHeader = true
        return this
    }

    fun setMarginTop(top: Int): GridSupportMatchAvgDivider {
        marginTop = RvUtils.dp2px(top)
        return this
    }

    private fun initAvgGap(parent: RecyclerView) {
        if (mGm != null) return
        mGm = parent.layoutManager as GridLayoutManager
        mSpanCount = mGm!!.spanCount
        oneLeft = getOneLeft()
        oneRight = getOneRight()
        middleLeft = getMiddleLeft()
    }


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        initAvgGap(parent)
        var position = parent.getChildAdapterPosition(view)
        val spanSize: Int = mGm!!.spanSizeLookup.getSpanSize(position)

//        Ls.d(
//            "getItemOffsets()..666..position=$position spanSize=$spanSize mSpanCount=$mSpanCount  " +
//                    "oneLeft=${oneLeft} oneRight=${oneRight} middleLeft=${middleLeft}  marginTop=${marginTop}"
//        )
        if (hasHeader) {
            if (position == 0) {
                return
            }
            position--
        }


        if (mSpanCount == spanSize) return
        val isFirstRow = position < mSpanCount
        when (position % mSpanCount) {
            0 -> {
                outRect.set(oneLeft, marginTop, oneRight, marginBottom)
            }
            1 -> {
                outRect.set(middleLeft, marginTop, middleLeft, marginBottom)
            }
            2 -> {
                outRect.set(oneRight,marginTop, oneLeft, marginBottom)
            }
        }
    }


    private fun getOneLeft(): Int {
        return mAvgGap
    }

    private fun getOneRight(): Int {
        return getMiddleLeft() * 2 - getOneLeft()
    }

    private fun getMiddleLeft(): Int {
        if (mSpanCount == 2) {
            return mAvgGap * 2 / 4
        }
        return mAvgGap * 2 / 3
    }


}

