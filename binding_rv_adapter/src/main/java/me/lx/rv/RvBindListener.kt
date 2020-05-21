package me.lx.rv

import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.loadmore.LoadMoreAdapter

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/11/11 14:06
 *  version: 1.0
 *  desc:
 */
interface RvBindListener<T> {



    /**
     * 获取item的布局文件
     * 需要返回 XmlItemBinding 或者 OnItemBindClass
     */
    fun getItemXmlObj(): Any

    /**
     * 获取列表数据源
     */
    fun getItems(): java.util.AbstractList<T>

    /**
     * 获取列表适配器
     */
    fun getAdapter(): BindingRecyclerViewAdapter<T>?{
        return null
    }

    fun getItemIds(): BindingRecyclerViewAdapter.ItemIds<T>? {
        return null
    }
    /**
     * 获取是否正在刷新中
     */
    fun getViewHolderFactory(): BindingRecyclerViewAdapter.ViewHolderFactory? {
        return null
    }

    /**
     * 获取列表分割线
     */
    fun getItemDecoration():RecyclerView.ItemDecoration? {
        return null
    }

    /**
     * 是否显示空布局
     */
    fun isShowEmptyLayoutCondition():ObservableBoolean?{
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

    /**
     * 获取是否正在刷新中
     */
    fun getSpanSizeLookup(): GridLayoutManager.SpanSizeLookup? {
        return null
    }

    /**
     * 如果是:2 , 说明是 grid_span in 2..11
     * 如果是:grid_span >= 12 , 说明是 StaggeredGridLayoutManager,  grid_span % 10
     * 获取布局管理器
     */
    fun getLayoutFlag(): Int {
        return RecyclerView.VERTICAL
    }
}