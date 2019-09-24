package me.lx.sample.group.model

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import me.lx.sample.ClickListeners
import me.lx.sample.group.adapter.GroupedListAdapter

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/20 11:05
 *  version: 1.0
 *  desc:
 */
class GroupAdapterModel : ViewModel(), ClickListeners {


    val dataList = ObservableArrayList<Int>()
    // 数据 -> item
    val groupList = GroupModel.getGroupOb(10, 3)

    val adapter = GroupedListAdapter()
    //  recyclerView.setAdapter(adapter)

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

    override fun clickAddItem() {
        if (groupList.isNotEmpty()) {
            groupList[0].children.add(GroupModel.getChildEntity(0, groupList[0].children.size))
        }

        // 插入
        // groupList[0].children.add(GroupModel.getChildEntity(0,groupList[0].children.size)) // onItemRangeInserted

//        val children = ObservableArrayList<ChildEntity>()
//        for (j in 0 until 3) {
//            children.add(GroupModel.getChildEntity(groupList.size, j))
//        }
//        groupList.add(GroupModel.getGroupEntity(groupList.size,children))

//        val childList = ObservableArrayList<ChildEntity>()
//        childList.add(GroupModel.getChildEntity(0,groupList[0].children.size))
//        childList.add(GroupModel.getChildEntity(0,groupList[0].children.size))
//        groupList[0].children.addAll(childList) // onItemRangeInserted

        // 移除
        // groupList[0].children.removeAt(0)
    }

    override fun clickRemoveItem() {
        //  groupList[1].children.add(GroupModel.getChildEntity(1,groupList[1].children.size))
        if (groupList.isNotEmpty() && groupList[0].children.size > 1) {
            groupList[0].children.removeAt(groupList[0].children.size - 1)
        }
    }
}