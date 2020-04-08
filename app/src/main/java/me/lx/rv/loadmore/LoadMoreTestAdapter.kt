package me.lx.rv.loadmore

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/12/31 21:14
 *  version: 1.0
 *  desc:
 */
class LoadMoreTestAdapter(var innerAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return innerAdapter.onCreateViewHolder(parent,viewType)
    }

    override fun getItemCount(): Int {
        return innerAdapter.itemCount
    }

    override fun getItemViewType(position: Int): Int {
        return innerAdapter.getItemViewType(position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        return innerAdapter.onAttachedToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        innerAdapter.onBindViewHolder(holder,position)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        innerAdapter.onBindViewHolder(holder, position, payloads)
    }


    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        innerAdapter.onViewAttachedToWindow(holder)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        innerAdapter.unregisterAdapterDataObserver(observer)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        innerAdapter.onViewDetachedFromWindow(holder)
    }

    override fun getItemId(position: Int): Long {
        return innerAdapter.getItemId(position)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        innerAdapter.setHasStableIds(hasStableIds)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return innerAdapter.onFailedToRecycleView(holder)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        innerAdapter.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        innerAdapter.onViewRecycled(holder)
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        innerAdapter.registerAdapterDataObserver(observer)
    }

}