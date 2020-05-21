package me.lx.rv.bindingadapter;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * author: luoXiong
 * e-mail: 382060748@qq.com
 * date: 2020/4/11 10:18
 * version: 1.0
 * desc:
 */
public class SwipeRefreshLayoutBindingAdapter {
    @androidx.databinding.BindingAdapter(value = {"rv_swipeRefreshLayout_refreshing"},requireAll = false)
    public static void set_rv_swipeRefreshLayout_refreshing(
            SwipeRefreshLayout refreshLayout,
            Boolean newValue) {
        if (refreshLayout.isRefreshing() != newValue) {
            refreshLayout.setRefreshing(newValue);
        }
    }


    @androidx.databinding.InverseBindingAdapter(attribute = "rv_swipeRefreshLayout_refreshing",
            event = "bind_swipeRefreshLayout_refreshingAttrChanged")
    public static boolean getSwipeRefreshLayoutRefreshing(
            SwipeRefreshLayout refreshLayout) {
        return refreshLayout.isRefreshing();
    }

    @androidx.databinding.BindingAdapter(requireAll = false, value = {"bind_swipeRefreshLayout_refreshingAttrChanged"})
    public static void setOnRefreshListener(
            SwipeRefreshLayout refreshLayout,
            final androidx.databinding.InverseBindingListener bindingListener) {
        if (bindingListener != null) {
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    bindingListener.onChange();
                }
            });
        }
    }
}
