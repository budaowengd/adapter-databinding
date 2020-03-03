package me.lx.rv.bindingadapter

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.tools.EmptyViewUtils

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/10/18 17:31
 *  version: 1.0
 *  desc:
 */

/**
 * 支持懒加载设置空布局,b 自动控制空布局的显示和隐藏
 */
@BindingAdapter(
    value = ["rv_item_decoration"],
    requireAll = false
)
fun set_rv_divider(
    rv: RecyclerView,
    divider: RecyclerView.ItemDecoration?
) {
   // Ls.d("set_rv_divider()...1111....divider=${divider?.hashCode()}")
    if (divider != null) {
        if (rv.itemDecorationCount > 0) {
            val decoration = rv.getItemDecorationAt(rv.itemDecorationCount-1)
            if (divider != decoration) {
                rv.addItemDecoration(divider)
            }
        } else {
            rv.addItemDecoration(divider)
        }
    }
}


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