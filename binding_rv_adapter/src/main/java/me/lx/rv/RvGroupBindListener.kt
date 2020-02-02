package me.lx.rv

import androidx.databinding.ObservableBoolean
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
interface RvGroupBindListener<T, C> {

    /**
     * 获取item的布局文件
     */
    //  fun getItemXmlObj(): XmlItemBinding<T>

    /**
     * 获取列表数据源
     */
    fun getGroups(): java.util.AbstractList<T>

    /**
     * 获取列表适配器
     */
    fun getAdapter(): GroupedRecyclerViewAdapter<T, C>

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

    fun getClickChildListener(): GroupedRecyclerViewAdapter.ClickGroupListener? {
        return null
    }

    fun getClickFootListener(): GroupedRecyclerViewAdapter.ClickGroupListener? {
        return null
    }

    fun getClickHeaderListener(): GroupedRecyclerViewAdapter.ClickGroupListener? {
        return null
    }
}