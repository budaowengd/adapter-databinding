package me.lx.rv.divider

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.tools.RvUtils

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/10/18 17:27
 *  version: 1.0
 *  desc: @link[androidx.recyclerview.widget.DividerItemDecoration]
 * 垂直列表分割线,可以设置每个item离上1个item的间距
 */
class LinearHasGapDivider : RecyclerView.ItemDecoration() {

    private var marginTop = 0
    private var marginLeft = 0
    private var marginRight = 0
    private var marginBottom = 0

    fun setMarginTop(space: Int): LinearHasGapDivider {
        marginTop = RvUtils.dp2px(space)
        return this
    }

    fun setMarginLeft(space: Int): LinearHasGapDivider {
        marginLeft = RvUtils.dp2px(space)
        return this
    }

    fun setMarginRight(space: Int): LinearHasGapDivider {
        marginRight = RvUtils.dp2px(space)
        return this
    }

    fun setMarginBottom(space: Int): LinearHasGapDivider {
        marginBottom = RvUtils.dp2px(space)
        return this
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(marginLeft, marginTop, marginRight, marginBottom)
    }
}