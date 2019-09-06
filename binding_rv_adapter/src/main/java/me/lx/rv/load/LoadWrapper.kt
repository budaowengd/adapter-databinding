package me.lx.rv.load

import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.loadmore.LoadMoreAdapter
import me.lx.rv.loadmore.LoadMoreWrapper

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/6 08:58
 *  version: 1.0
 *  desc:
 */
class LoadWrapper {
    companion object {
        fun with(adapter: RecyclerView.Adapter<*>): LoadMoreWrapper {
            val loadMoreAdapter = LoadMoreAdapter(adapter)
            return LoadMoreWrapper(loadMoreAdapter)
        }
    }
}