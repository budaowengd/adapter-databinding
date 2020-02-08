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
class TwoLevelChildListChangedCallback(
    private var adapter: TwoLevelGroupedRecyclerViewAdapter<*, *, *>,
    private var isChildGroup: Boolean
) : ObservableList.OnListChangedCallback<ObservableList<*>>() {
    override fun onChanged(sender: ObservableList<*>) {
        if (TwoLevelGroupedRecyclerViewAdapter.DEBUG) {
            Ls.d("TwoLevelChildListChangedCallback......onChanged()..1111....")
        }

        adapter.notifyDataChanged()
    }

    override fun onItemRangeRemoved(sender: ObservableList<*>, positionStart: Int, itemCount: Int) {
        if (TwoLevelGroupedRecyclerViewAdapter.DEBUG) {
            Ls.d(
                "TwoLevelChildListChangedCallback...onItemRangeRemoved().22..positionStart=$positionStart  itemCount=$itemCount" +
                        " senderSize=${sender.size} isChildGroup=$isChildGroup"
            )
        }
        if (sender.isEmpty()) {
            // 如果是组里的child分组
            if (isChildGroup) {
                // 从第1层移除当前child分组
                val groupPosition = adapter.getGroupPositionByChildGroupList(sender)
                adapter.removeGroupPosition(groupPosition)
            } else {
                // 如果最里层子项都没了,把当前child组也移除掉
                val groupPosition = adapter.getGroupPositionByChildChildList(sender)
                adapter.removeChildGroupPosition(groupPosition.first, groupPosition.second)
            }
        }
        adapter.notifyDataChanged()
    }

    override fun onItemRangeMoved(
        sender: ObservableList<*>,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
        if (TwoLevelGroupedRecyclerViewAdapter.DEBUG) {
            Ls.d("TwoLevelChildListChangedCallback...onItemRangeMoved()..3333...fromPosition=$fromPosition  itemCount=$itemCount")
        }
        adapter.notifyDataChanged()
    }

    override fun onItemRangeInserted(
        sender: ObservableList<*>,
        positionStart: Int,
        itemCount: Int
    ) {
        if (TwoLevelGroupedRecyclerViewAdapter.DEBUG) {
            Ls.d("TwoLevelChildListChangedCallback. onItemRangeInserted()..44..positionStart=$positionStart  itemCount=$itemCount " +
                    "sender=${sender.size} isChildGroup=$isChildGroup")
        }
        if (isChildGroup) {
            adapter.registerChildChildListChangedCallback2(sender[positionStart])
        }
        adapter.notifyDataChanged()
    }


    override fun onItemRangeChanged(sender: ObservableList<*>, positionStart: Int, itemCount: Int) {
        if (TwoLevelGroupedRecyclerViewAdapter.DEBUG) {
            Ls.d("TwoLevelChildListChangedCallback......onItemRangeChanged()..5555..positionStart=$positionStart  itemCount=$itemCount")
        }
        adapter.notifyDataChanged()
    }
}