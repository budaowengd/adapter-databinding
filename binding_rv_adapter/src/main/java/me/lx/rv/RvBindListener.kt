package me.lx.rv

import androidx.databinding.ObservableBoolean
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
    fun getItemBinding(): Any

    /**
     * 获取列表数据源
     */
    fun getItems(): java.util.AbstractList<T>

    /**
     * 获取列表适配器
     */
    fun getAdapter(): BindingRecyclerViewAdapter<T>

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
     * 获取布局管理器
     */
    fun getLayoutManager(): RecyclerView.LayoutManager? {
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
    fun getIsRefreshingOb(): ObservableBoolean? {
        return null
    }


}