package me.lx.sample.model

import androidx.core.util.Consumer
import androidx.lifecycle.ViewModel
import me.lx.rv.group.BaseFun1ClickGroupListener
import me.lx.sample.group.adapter.ThreeLevelGroupAdapter
import me.lx.sample.group.entity.ChildGroupEntity
import me.lx.sample.group.model.GroupModel

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2020/1/17 14:06
 *  version: 1.0
 *  desc: 2级分组,child -> 包含child
 */
class TwoLevelGroupModel : ViewModel() {
    // 数据 -> item
    // 数据
    val groupList = GroupModel.getTwoGroupGroupOb(4, 2) // 普通列表数据

//    val groupAdapter = GroupedListAdapter()


    var childGroupFooterClickCallback: Consumer<ChildGroupEntity>? = null

    val groupAdapter = ThreeLevelGroupAdapter().apply {
        childGroupFooterClickEvent = object : BaseFun1ClickGroupListener<ChildGroupEntity>() {
            override fun clickGroup(group: ChildGroupEntity) {
                childGroupFooterClickCallback?.accept(group)
            }
        }
        setChildGroupEmptyRemoveGroup(true)
    }
}