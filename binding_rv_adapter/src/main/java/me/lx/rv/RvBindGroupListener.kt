package me.lx.rv

import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.group.GroupedRecyclerViewAdapter
import me.lx.rv.loadmore.LoadMoreAdapter

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/11/11 14:06
 *  version: 1.0
 *  desc:
 */
interface RvBindGroupListener<CG, C> {
    /**
     * 获取列表数据源
     */
    fun getGroupList(): java.util.AbstractList<CG>

    /**
     * 获取列表适配器
     */
    fun getGroupAdapter(): GroupedRecyclerViewAdapter<CG, C>

    /**
     * 获取列表分割线
     */
    fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return null
    }

    /**
     * 是否显示空布局
     */
    fun isShowEmptyLayoutCondition(): ObservableBoolean? {
        return null
    }

    /**
     * 获取布局管理器
     */
    fun getLayoutFlag(): Int {
        return RecyclerView.VERTICAL
    }
    fun getSpanSizeLookup(): GridLayoutManager.SpanSizeLookup? {
        return null
    }
    /**
     * 获取加载更多的监听
     */
    fun getLoadMoreListener(): LoadMoreAdapter.LoadMoreListener? {
        return null
    }

    /**
     * 获取是否正在刷新中
     */
    fun getRefreshingOb(): ObservableBoolean? {
        return null
    }

    fun getClickChildListener(): me.lx.rv.click.ClickListener? {
        return null
    }

    fun getClickFootListener(): me.lx.rv.click.ClickListener? {
        return null
    }

    fun getClickHeaderListener(): me.lx.rv.click.ClickListener? {
        return null
    }
}