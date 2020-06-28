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
class Group3CgAndCcListChangCallback(
        private var adapter: Group3RecyclerViewAdapter<*, *, *>,
        private var isCg: Boolean
) : ObservableList.OnListChangedCallback<ObservableList<*>>() {
    override fun onChanged(sender: ObservableList<*>) {
        if (Group3RecyclerViewAdapter.DEBUG) {
            Ls.d("Group3CgAndCcListChangCallback......onChanged()..1111....")
        }

        adapter.notifyDataChanged()
    }

    override fun onItemRangeRemoved(sender: ObservableList<*>, positionStart: Int, itemCount: Int) {
        if (Group3RecyclerViewAdapter.DEBUG) {
            Ls.d(
                    "Group3CgAndCcListChangCallback...onItemRangeRemoved().22..positionStart=$positionStart  itemCount=$itemCount" +
                            " senderSize=${sender.size} isCg=$isCg"
            )
        }
        if (sender.isEmpty()) {
            // 如果是组里的child分组
            if (isCg) {
                // 从第1层移除当前child分组
                val groupPosition = adapter.getGroupPositionByCgList(sender)
                if (adapter.childEmptyIsRemoveHeader) {
                    adapter.removeGroupPosition(groupPosition)
                }
            } else {
                // 如果最里层子项都没了,把当前child组也移除掉
                val groupPosition = adapter.getGroupPositionByCcList(sender)
                if (adapter.ccEmptyRemoveCg) {
                    adapter.removeCgPosition(groupPosition.first, groupPosition.second)
                }
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
        if (Group3RecyclerViewAdapter.DEBUG) {
            Ls.d("Group3CgAndCcListChangCallback...onItemRangeMoved()..3333...fromPosition=$fromPosition  itemCount=$itemCount")
        }
        adapter.notifyDataChanged()
    }

    override fun onItemRangeInserted(
            sender: ObservableList<*>,
            positionStart: Int,
            itemCount: Int
    ) {
        if (Group3RecyclerViewAdapter.DEBUG) {
            Ls.d("Group3CgAndCcListChangCallback. onItemRangeInserted()..44..positionStart=$positionStart  itemCount=$itemCount " +
                    "sender=${sender.size} isCg=$isCg")
        }
        if (isCg) {
            adapter.registerCcListChangedCallback2(sender[positionStart])
        }
        adapter.notifyDataChanged()
    }


    override fun onItemRangeChanged(sender: ObservableList<*>, positionStart: Int, itemCount: Int) {
        if (Group3RecyclerViewAdapter.DEBUG) {
            Ls.d("Group3CgAndCcListChangCallback......onItemRangeChanged()..5555..positionStart=$positionStart  itemCount=$itemCount")
        }
        adapter.notifyDataChanged()
    }
}