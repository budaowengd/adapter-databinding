package me.lx.sample.group.model

import androidx.lifecycle.ViewModel
import me.lx.sample.ClickListeners
import me.lx.sample.group.adapter.GroupedListAdapter
import me.lx.sample.group.adapter.NoHeaderAdapter

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/20 11:05
 *  version: 1.0
 *  desc:
 */
class GroupAdapterModel : ViewModel(), ClickListeners {

    // 数据
    val groupList = GroupModel.getGroupOb(10, 3)

    // 分组列表适配器
    val groupAdapter = GroupedListAdapter()

    val noHeaderAdapter = NoHeaderAdapter()


    override fun clickAddItem() {
        if (groupList.isNotEmpty()) {
            groupList[0].childList.add(GroupModel.getChildEntity(0, groupList[0].childList.size))
        }
    }

    override fun clickRemoveItem() {
        if (groupList.isNotEmpty() && groupList[0].childList.size > 1) {
            groupList[0].childList.removeAt(groupList[0].childList.size - 1)
        }
    }
}