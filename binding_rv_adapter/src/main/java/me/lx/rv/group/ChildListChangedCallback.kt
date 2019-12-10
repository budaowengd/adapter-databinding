package me.lx.rv.group

import android.annotation.SuppressLint
import androidx.databinding.ObservableList
import me.lx.rv.tools.Ls

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/24 11:06
 *  version: 1.0
 *  desc:
 */
@SuppressLint("LongLogTag")
class ChildListChangedCallback<T, C>(
    private var adapter: GroupedRecyclerViewAdapter<T, C>,
    private var childEmptyIsRemoveHeader: Boolean? = null
) :
    ObservableList.OnListChangedCallback<ObservableList<C>>() {
    override fun onChanged(sender: ObservableList<C>) {
        if (GroupedRecyclerViewAdapter.DEBUG) {
            Ls.d("ChildListChangedCallback......onChanged()..1111....")
        }

        val groupPosition = adapter.getGroupPositionByChildList(sender)
        adapter.notifyChildRangeChanged(groupPosition, 0, sender.size)
    }

    override fun onItemRangeRemoved(sender: ObservableList<C>, positionStart: Int, itemCount: Int) {
        val groupPosition = adapter.getGroupPositionByChildList(sender)
        if (GroupedRecyclerViewAdapter.DEBUG) {
            Ls.d(
                "ChildListChangedCallback......onItemRangeRemoved()..2222..positionStart=$positionStart  itemCount=$itemCount  " +
                        "itemsSize=${sender.size}  groupPosition=$groupPosition childEmptyIsRemoveHeader=$childEmptyIsRemoveHeader"
            )
        }
        adapter.notifyChildRangeRemoved(groupPosition, positionStart, itemCount)
        // 如果孩子列表为空了,会崩溃...记住,得先刷新头部数据
        if (sender.size == 0) {
            if (childEmptyIsRemoveHeader == true) {
                (adapter.getItems() as ObservableList).removeAt(groupPosition)

            } else {
                adapter.notifyHeaderChanged(groupPosition)
            }
        }
    }

    override fun onItemRangeMoved(
        sender: ObservableList<C>,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
        if (GroupedRecyclerViewAdapter.DEBUG) {
            Ls.d("ChildListChangedCallback......onItemRangeMoved()..3333...fromPosition=$fromPosition  itemCount=$itemCount")
        }
        val groupPosition = adapter.getGroupPositionByChildList(sender)
        if (sender.size == 0) {
            if (childEmptyIsRemoveHeader == true) {
                (adapter.getItems() as ObservableList).removeAt(groupPosition)
            } else {
                adapter.notifyHeaderChanged(groupPosition)
            }
        }
        adapter.notifyChildRangeRemoved(groupPosition, fromPosition, itemCount)
    }

    override fun onItemRangeInserted(
        sender: ObservableList<C>,
        positionStart: Int,
        itemCount: Int
    ) {
        if (GroupedRecyclerViewAdapter.DEBUG) {
            Ls.d("ChildListChangedCallback......onItemRangeInserted()..4444..positionStart=$positionStart  itemCount=$itemCount")
        }
        val groupPosition = adapter.getGroupPositionByChildList(sender)
        adapter.notifyChildInserted(groupPosition, positionStart)
    }


    override fun onItemRangeChanged(sender: ObservableList<C>, positionStart: Int, itemCount: Int) {
        if (GroupedRecyclerViewAdapter.DEBUG) {
            Ls.d("ChildListChangedCallback......onItemRangeChanged()..5555..positionStart=$positionStart  itemCount=$itemCount")
        }
        val groupPosition = adapter.getGroupPositionByChildList(sender)
        adapter.notifyChildRangeChanged(groupPosition, positionStart, itemCount)
    }
}