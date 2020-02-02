package me.lx.rv.loadmore

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * author: luoXiong
 * e-mail: 382060748@qq.com
 * date: 2019/9/6 19:01
 * version: 1.0
 * desc: 加载更多的ViewHolder, 实现了ILoadMore接口后,在父类AbstractLoadMoreFooter去
 * 具体的实现.易于扩展
 */
internal class LoadMoreViewHolder(itemView: View, footer: AbstractLoadMoreFooter) : RecyclerView.ViewHolder(itemView),
    ILoadMore {
    private val mFooter: AbstractLoadMoreFooter

    init {
        val params = itemView.layoutParams
        if (params is StaggeredGridLayoutManager.LayoutParams) {
            params.isFullSpan = true
        }
        mFooter = footer
        footer.onCreate(itemView)
    }

    fun setState(stateType: Int) {
        when (stateType) {
            LoadMoreAdapter.STATE_LOADING -> loading()
            LoadMoreAdapter.STATE_NO_MORE_DATA -> noMoreData()
            LoadMoreAdapter.STATE_LOAD_FAILED -> loadFailed()
            else -> {
                mFooter.hideLoadMore()
            }
        }
    }

    override fun loading() {
        mFooter.loading()
    }

    override fun noMoreData() {
        mFooter.noMoreData()
    }

    override fun loadFailed() {
        mFooter.loadFailed()
    }


}