package me.lx.rv.bindingadapter

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.tools.EmptyViewUtils
import me.lx.rv.tools.RvUtils

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/10/18 17:31
 *  version: 1.0
 *  desc:
 */

/**
 * 设置绑定View的适配器,
 *分割线如果比LayoutManager或者SpanSizeLookup先设置,会出现布局错乱
 *因为分割线里需要获取LayoutManager,进行判断
 *所以分割线的设置要在LayoutManager 后面
 */
//@BindingAdapter(
//    value = [ "rv_item_decoration", "rv_layout_span", "rv_SpanSizeLookup"],
//    requireAll = false
//)
//fun <T> set_rv_divider(
//    rv: RecyclerView,
//    divider: RecyclerView.ItemDecoration?,
//    grid_span: Int? = null,
//    spanLookup: GridLayoutManager.SpanSizeLookup? = null
//) {
//
//    if (divider != null) {
//        if (rv.itemDecorationCount > 0) {
//            val decoration = rv.getItemDecorationAt(rv.itemDecorationCount - 1)
//            if (divider != decoration) {
//                rv.addItemDecoration(divider)
//            }
//        } else {
//            rv.addItemDecoration(divider)
//        }
//    }
//}


/**
 * 支持懒加载设置空布局,b 自动控制空布局的显示和隐藏
 */
@BindingAdapter(
    value = ["rv_support_is_show_empty_view", "rv_support_empty_view_layoutId",
        "rv_support_empty_text_hint", "rv_support_empty_img"],
    requireAll = false
)
fun set_rv_support_is_show_empty_view(
    emptyViewContain: ViewGroup,
    isShowEmpty: Boolean?, @LayoutRes emptyLayoutId: Int = 0,
    textHint: String? = null, imgData: Any? = null
) {
    isShowEmpty ?: return
    EmptyViewUtils.showOrHideEmptyView(emptyViewContain, isShowEmpty, emptyLayoutId, textHint, imgData)
}