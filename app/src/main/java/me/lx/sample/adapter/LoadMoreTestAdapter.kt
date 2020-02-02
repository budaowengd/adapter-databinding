package me.lx.sample.adapter

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
}