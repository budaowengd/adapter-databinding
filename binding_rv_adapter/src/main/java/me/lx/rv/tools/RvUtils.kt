package me.lx.rv.tools

import android.content.res.Resources
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/12/27 21:09
 *  version: 1.0
 *  desc:
 */
class RvUtils {
    companion object{
        fun dp2px(dpValue: Number): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (dpValue.toFloat() * scale + 0.5f).toInt()
        }
        /**
         * 不能使用BindingAdapter的原因是,这里会比setAdapter后执行,在loadmoreAdapter里获取不到Lookup对象
         * , "rv_group_layoutmanager"
         *  rv_layoutmanager  rv_layout_is_horizontal
         * @param layoutHorizontal
         * @param grid_span 如果是1, 创建 LinearLayout 垂直方向
         *          如果==0,会创建 LinearLayout,并设置 水平方向
         *          如果大于1,会创建 GridLayout,并设置spanCount
         *          如果是负数,不设置layout
         *          如果大于等于12,说明是瀑布流布局,取个位数作为spanCount
         *          GridLayoutManager.SpanSizeLookup
         */
        fun set_rv_layoutmanager(
            rv: RecyclerView,
            layoutSpan: Int? = null,
            spanLookup: GridLayoutManager.SpanSizeLookup? = null
        ) {
            //Ls.d("set_rv_layoutmanager().....grid_span=$grid_span")
            var layout: RecyclerView.LayoutManager? = null
            val grid_span = layoutSpan ?: RecyclerView.VERTICAL
            if (grid_span == RecyclerView.VERTICAL || grid_span == RecyclerView.HORIZONTAL) {
                layout = LinearLayoutManager(rv.context, grid_span, false)
            } else if (grid_span in 2..11) {
                layout = GridLayoutManager(rv.context, grid_span)
                if (spanLookup != null) {
                    layout.spanSizeLookup = spanLookup
                }
            } else if (grid_span >= 12) {
                val span = grid_span % 10
                layout = StaggeredGridLayoutManager(span, RecyclerView.VERTICAL)
            }
            if (layout != null) {
                rv.layoutManager = layout
            }
        }
        fun  set_rv_divider(
            rv: RecyclerView,
            divider: RecyclerView.ItemDecoration?
        ) {

            if (divider != null) {
                if (rv.itemDecorationCount > 0) {
                    val decoration = rv.getItemDecorationAt(rv.itemDecorationCount - 1)
                    if (divider != decoration) {
                        rv.addItemDecoration(divider)
                    }
                } else {
                    rv.addItemDecoration(divider)
                }
            }
        }

    }
}