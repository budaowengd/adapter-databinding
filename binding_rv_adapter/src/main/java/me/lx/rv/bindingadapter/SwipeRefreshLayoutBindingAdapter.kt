//package me.lx.rv.bindingadapter
//
//import androidx.databinding.BindingAdapter
//import androidx.databinding.InverseBindingAdapter
//import androidx.databinding.InverseBindingListener
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
//
///**
// *  author: luoXiong
// *  e-mail: 382060748@qq.com
// *  date: 2019/11/11 15:53
// *  version: 1.0
// *  desc:
// */
//object SwipeRefreshLayoutBinding {
//    @JvmStatic
//    @BindingAdapter(
//        value = ["rv_swipeRefreshLayout_refreshing"]
//    )
//    fun set_rv_swipeRefreshLayout_refreshing(
//        refreshLayout: SwipeRefreshLayout,
//        newValue: Boolean?
//    ) {
//        if (refreshLayout.isRefreshing != newValue&&newValue!=null) {
//            refreshLayout.isRefreshing = newValue
//        }
//    }
//
//    @JvmStatic
//    @InverseBindingAdapter(
//        attribute = "app:rv_swipeRefreshLayout_refreshing",
//        event = "app:bind_swipeRefreshLayout_refreshingAttrChanged"
//    )
//    fun getSwipeRefreshLayoutRefreshing(refreshLayout: SwipeRefreshLayout): Boolean = refreshLayout.isRefreshing
//
//    @JvmStatic
//    @BindingAdapter(
//        "app:bind_swipeRefreshLayout_refreshingAttrChanged",
//        requireAll = false
//    )
//    fun setOnRefreshListener(
//        refreshLayout: SwipeRefreshLayout,
//        bindingListener: InverseBindingListener?
//    ) {
//        if (bindingListener != null)
//            refreshLayout.setOnRefreshListener {
//                bindingListener.onChange()
//            }
//    }
//}
//
