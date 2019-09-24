package me.lx.sample.group.model

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/24 20:39
 *  version: 1.0
 *  desc:
 */
class TestDataChanged {
    val dataList = ObservableArrayList<Int>()
    init {
        dataList.addOnListChangedCallback(object :
            ObservableList.OnListChangedCallback<ObservableArrayList<Int>>() {
            override fun onChanged(sender: ObservableArrayList<Int>?) {
                println("dataList onChanged()...1111....sender=${sender?.size}")
            }

            override fun onItemRangeRemoved(
                sender: ObservableArrayList<Int>?,
                positionStart: Int,
                itemCount: Int
            ) {
                println("dataList onItemRangeRemoved()...2222....positionStart=$positionStart  itemCount=$itemCount")
            }

            override fun onItemRangeMoved(
                sender: ObservableArrayList<Int>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                println("dataList onItemRangeMoved()...3333....fromPosition=$fromPosition  toPosition=$toPosition  itemCount=$itemCount")
            }

            override fun onItemRangeInserted(
                sender: ObservableArrayList<Int>?,
                positionStart: Int,
                itemCount: Int
            ) {
                println("dataList onItemRangeInserted()...4444444....positionStart=$positionStart  itemCount=$itemCount")
            }

            override fun onItemRangeChanged(
                sender: ObservableArrayList<Int>?,
                positionStart: Int,
                itemCount: Int
            ) {
                println("dataList onItemRangeChanged()...555555....positionStart=$positionStart  itemCount=$itemCount")
            }
        })
    }

}