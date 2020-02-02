package me.lx.sample.group.model

import android.widget.Toast
import androidx.lifecycle.ViewModel
import me.lx.rv.group.BaseFun1ClickGroupListener
import me.lx.sample.ClickListeners
import me.lx.sample.MyApp
import me.lx.sample.group.adapter.*
import me.lx.sample.group.entity.ChildEntity
import me.lx.sample.group.entity.ExpandableGroupEntity
import me.lx.sample.group.entity.GroupEntity

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

    // 点击事件
    val clickChildEvent = object : BaseFun1ClickGroupListener<ChildEntity>() {
        override fun clickGroupItem(item: ChildEntity) {
            Toast.makeText(MyApp.sContext, item.child, Toast.LENGTH_LONG).show()
        }
    }

    val clickHeaderEvent = object : BaseFun1ClickGroupListener<GroupEntity>() {
        override fun clickGroupItem(item: GroupEntity) {
            Toast.makeText(MyApp.sContext, item.header, Toast.LENGTH_LONG).show()
        }
    }
    val clickFooterEvent = object : BaseFun1ClickGroupListener<GroupEntity>() {
        override fun clickGroupItem(item: GroupEntity) {
            Toast.makeText(MyApp.sContext, item.footer, Toast.LENGTH_LONG).show()
        }
    }

    val clickExpandHeaderEvent = object : BaseFun1ClickGroupListener<ExpandableGroupEntity>() {
        override fun clickGroupItem(item: ExpandableGroupEntity) {
            println("点击前 .....isExpand=" + item.isExpand)
            if (item.isExpand) {
                expandableAdapter.collapseGroup(item)
            } else {
                expandableAdapter.expandGroup(item)
            }
        }
    }

    // 适配器
    val groupAdapter = GroupedListAdapter()// 带头带尾

    val noHeaderAdapter = NoHeaderAdapter()  // 没有头

    val noFooterAdapter = NoFooterAdapter()// 没有尾

    val variousAdapter = VariousAdapter()// 头、子项、尾都是多类型的

    val variousChildAdapter = VariousChildAdapter()// 头、子项、尾都是多类型的

    val expandableAdapter = ExpandableAdapter() // 可展开、收起
    // val gridChildAdapter = grid() // 可展开、收起


    // 布局管理管理器


    override fun clickAddItem() {
        groupList.apply {
            if (isNotEmpty()) {
                get(0).childList.add(GroupModel.getChildEntity(0, get(0).childList.size))
            }
        }
    }

    override fun clickRemoveItem() {
        groupList.apply {
            if (isNotEmpty()&&get(0).childList.isNotEmpty()) {
                get(0).childList.removeAt(get(0).childList.size - 1)
            }
        }
    }

    override fun clickExpandAdapterAddItem() {
        expandGroupList.apply {
            get(0).childList.add(GroupModel.getChildEntity(0, get(0).childList.size))
        }
    }

    override fun clickExpandAdapterRemoveItem() {
        expandGroupList.apply {
            if (get(0).childList.size > 1) {
                get(0).childList.removeAt(get(0).childList.size - 1)
            }
        }
    }
}