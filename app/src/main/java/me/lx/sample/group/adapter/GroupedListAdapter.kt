package me.lx.sample.group.adapter

import androidx.databinding.ViewDataBinding
import me.lx.rv.group.GroupedRecyclerViewAdapter
import me.lx.sample.R
import me.lx.sample.group.entity.ChildEntity
import me.lx.sample.group.entity.GroupEntity

/**
 * 这是普通的分组Adapter 每一个组都有头部、尾部和子项。
 */
open class GroupedListAdapter : GroupedRecyclerViewAdapter<GroupEntity, ChildEntity>() {

    //    public GroupedListAdapter(@NotNull List<GroupEntity> dataList) {
    //        super(dataList);
    //    }

    override fun getChildrenList(groupItem: GroupEntity): List<ChildEntity> {
        return groupItem.childList
    }

    override fun getChildrenCount(groupPosition: Int, groupItem: GroupEntity): Int {
        return groupItem.childList.size
    }
    override fun hasHeader(groupPosition: Int): Boolean {
        return getItems()[groupPosition].childList.size > 0
    }

    override fun hasFooter(groupPosition: Int): Boolean {
        return getItems()[groupPosition].childList.size > 0
    }

    override fun getHeaderLayout(viewType: Int): Int {
        return R.layout.adapter_header
    }

    override fun getFooterLayout(viewType: Int): Int {
        return R.layout.adapter_footer
    }

    override fun getChildLayout(viewType: Int): Int {
        return R.layout.adapter_child
    }

    override fun onBindHeaderViewHolder(binding: ViewDataBinding, groupItem: GroupEntity, groupPosition: Int) {
    }

    override fun onBindFooterViewHolder(binding:ViewDataBinding,  groupItem: GroupEntity,groupPosition: Int) {
    }

    override fun onBindChildViewHolder(binding:ViewDataBinding, groupItem: GroupEntity, child: ChildEntity,groupPosition: Int,childPosition: Int) {
        // 而外设置变量
    }




}
