package me.lx.rv.group

import androidx.databinding.ObservableList

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/24 11:06
 *  version: 1.0
 *  desc:
 */
class ChildListChangedCallback<T, C>(private var adapter: GroupedRecyclerViewAdapter<T, C>) :
    ObservableList.OnListChangedCallback<ObservableList<C>>() {
    override fun onChanged(sender: ObservableList<C>) {
        // adapter.isDataChanged = true
        println("ChildListChangedCallback()....onChanged()...1111..")
        val groupPosition = adapter.getGroupPositionByChildPosition(sender)
        adapter.notifyChildRangeChanged(groupPosition, 0, sender.size)
    }

    override fun onItemRangeRemoved(sender: ObservableList<C>, positionStart: Int, itemCount: Int) {
        // adapter.isDataChanged = true
        val groupPosition = adapter.getGroupPositionByChildPosition(sender)
        println("ChildListChangedCallback()....onItemRangeRemoved()...22222...positionStart=$positionStart  size=${sender.size} groupPosition=$groupPosition")
        // 如果孩子列表为空了,记住,得先刷新头部数据
        if (sender.size == 0) {
            adapter.notifyHeaderChanged(groupPosition)
        }
        adapter.notifyChildRangeRemoved(groupPosition, positionStart, itemCount)
    }

    override fun onItemRangeMoved(sender: ObservableList<C>, fromPosition: Int, toPosition: Int, itemCount: Int) {
        // adapter.isDataChanged = true
        println("ChildListChangedCallback()....onItemRangeMoved()...3333333..")
        val groupPosition = adapter.getGroupPositionByChildPosition(sender)
        if (sender.size == 0) {
            adapter.notifyHeaderChanged(groupPosition)
        }
        adapter.notifyChildRangeRemoved(groupPosition, fromPosition, itemCount)
    }

    override fun onItemRangeInserted(sender: ObservableList<C>, positionStart: Int, itemCount: Int) {
        // adapter.isDataChanged = true
        val groupPosition = adapter.getGroupPositionByChildPosition(sender)
        println("ChildListChangedCallback()....onItemRangeInserted()...444444..positionStart=$positionStart  groupPosition=$groupPosition  itemCount=$itemCount")
        adapter.notifyChildInserted(groupPosition, positionStart)
    }


    override fun onItemRangeChanged(sender: ObservableList<C>, positionStart: Int, itemCount: Int) {
        // adapter.isDataChanged = true
        println("ChildListChangedCallback()....onItemRangeChanged()...555555...positionStart=$positionStart")
        val groupPosition = adapter.getGroupPositionByChildPosition(sender)
        adapter.notifyChildRangeChanged(groupPosition, positionStart, itemCount)
    }
}