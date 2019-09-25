package me.lx.sample.group.model

import androidx.lifecycle.ViewModel
import me.lx.sample.ClickListeners
import me.lx.sample.group.adapter.*

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/20 11:05
 *  version: 1.0
 *  desc:
 */
class GroupAdapterModel : ViewModel(), ClickListeners {

    // 数据
    val groupList = GroupModel.getGroupOb(10, 3) // 普通列表数据
    val expandGroupList = GroupModel.getExpandableGroups(10, 3)// 展开、收起列表数据

    // 适配器
    val groupAdapter = GroupedListAdapter()// 带头带尾

    val noHeaderAdapter = NoHeaderAdapter()  // 没有头

    val noFooterAdapter = NoFooterAdapter()// 没有尾

    val variousAdapter = VariousAdapter()// 头、子项、尾都是多类型的

    val variousChildAdapter = VariousChildAdapter()// 头、子项、尾都是多类型的

    val expandableAdapter = ExpandableAdapter() // 可展开、收起


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