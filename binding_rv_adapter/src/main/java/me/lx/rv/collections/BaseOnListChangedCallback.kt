package me.lx.rv.collections

import androidx.databinding.ObservableList

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/10/27 19:52
 *  version: 1.0
 *  desc:
 */
abstract class BaseOnListChangedCallback<T> : ObservableList.OnListChangedCallback<ObservableList<T>>() {
    override fun onChanged(sender: ObservableList<T>) {
        itemChanged(sender)
    }

    override fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        itemChanged(sender)
    }

    override fun onItemRangeMoved(sender: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
        itemChanged(sender)
    }

    override fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        itemChanged(sender)
    }

    override fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        itemChanged(sender)
    }

    abstract fun itemChanged(sender: ObservableList<T>)
}