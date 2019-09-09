package me.lx.rv.loadmore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.R
import me.lx.rv.ext.itemBindingOf

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/6 09:01
 *  version: 1.0
 *  desc:
 */
class LoadMoreAdapter(private var mRawAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val type_load_more = 0
    }

    private var mInflater: LayoutInflater? = null
    @LayoutRes
    private var mLoadMoreRedId = R.layout.rv_load_more_layout

    private var mLoadMoreView: View? = null


    private var mIsLoading: Boolean = false

    // 布局 ->单一的
    val simpleItemBinding = itemBindingOf<Int>(R.layout.rv_load_more_layout)

    private val mDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            mIsLoading = false
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            mIsLoading = false
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            mIsLoading = false
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mIsLoading = false
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mIsLoading = false
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mIsLoading = false
        }
    }

    init {
        registerAdapterDataObserver(mDataObserver)
    }

    override fun getItemCount(): Int {
        return mRawAdapter.itemCount + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mRawAdapter.itemCount) {
            type_load_more
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == type_load_more) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.context)
            }
            val loadMoreBinding = DataBindingUtil.inflate(mInflater!!, mLoadMoreRedId, parent, false) as ViewDataBinding
            return object : RecyclerView.ViewHolder(loadMoreBinding.root) {}
        }
        return mRawAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        mRawAdapter.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mRawAdapter.onBindViewHolder(holder, position)
    }


    private var mPageSize = 0

    fun setPageSize(pageSize: Int) {
        mPageSize = pageSize
    }


    val type_1 = 1
    val type_2 = 1
    val type_3 = 1


}