package me.lx.rv.loadmore

import androidx.recyclerview.widget.RecyclerView

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/6 08:58
 *  version: 1.0
 *  desc:
 */
class LoadMoreWrapper {

    fun setPageSize(){

    }

    companion object {
        fun with(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>): LoadMoreAdapter {
            val loadMoreAdapter = LoadMoreAdapter(adapter)
            // return LoadMoreWrapper()
            return loadMoreAdapter
        }
    }
}