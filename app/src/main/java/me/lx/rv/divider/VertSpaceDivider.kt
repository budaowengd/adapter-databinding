package me.lx.rv.divider

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/10/18 17:27
 *  version: 1.0
 *  desc: @link[androidx.recyclerview.widget.DividerItemDecoration]
 * 垂直列表分割线,可以设置每个item离上1个item的间距
 */
class VerticalSpaceDivider : RecyclerView.ItemDecoration() {

    private var marginTop = 0

    fun setMarginTop(@Px topSpace: Int): VerticalSpaceDivider {
        marginTop = topSpace
        return this
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(0, marginTop, 0, 0)
    }
}