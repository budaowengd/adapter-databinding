package me.lx.rv.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/11/11 15:53
 *  version: 1.0
 *  desc:
 */
@BindingAdapter(
        value = ["rv_swipeRefreshLayout_refreshing"]
)
fun rv_swipeRefreshLayout_refreshing(
        swipeRefreshLayout: SwipeRefreshLayout,
        isRefreshing: Boolean?
) {
    if (isRefreshing != null) {
        swipeRefreshLayout.isRefreshing = isRefreshing
    }
    ///EmptyViewUtils.showOrHideEmptyView(emptyViewContain, isShowEmpty, emptyLayoutId, emptyTextHint)
}


@InverseBindingAdapter(
        attribute = "app:rv_swipeRefreshLayout_refreshing",
        event = "app:bind_swipeRefreshLayout_refreshingAttrChanged"
)
fun isSwipeRefreshLayoutRefreshing(swipeRefreshLayout: SwipeRefreshLayout): Boolean = swipeRefreshLayout.isRefreshing


@BindingAdapter(
        "app:bind_swipeRefreshLayout_refreshingAttrChanged",
        requireAll = false
)
fun setOnRefreshListener(
        swipeRefreshLayout: SwipeRefreshLayout,
        bindingListener: InverseBindingListener?
) {
    if (bindingListener != null)
        swipeRefreshLayout.setOnRefreshListener {
            bindingListener.onChange()
        }
}