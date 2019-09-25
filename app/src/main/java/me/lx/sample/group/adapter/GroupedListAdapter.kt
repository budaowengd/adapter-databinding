package me.lx.sample.group.adapter

import me.lx.rv.group.BaseViewHolder
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

    override fun getChildrenList(entity: GroupEntity): List<ChildEntity> {
        return entity.childList
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val childList = getItems()[groupPosition].childList
        return childList.size
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

    override fun onBindHeaderViewHolder(holder: BaseViewHolder, groupPosition: Int) {
        val entity = getItems()[groupPosition]
        holder.setText(R.id.tv_header, entity.header)
    }

    override fun onBindFooterViewHolder(holder: BaseViewHolder, groupPosition: Int) {
        val entity = getItems()[groupPosition]
        holder.setText(R.id.tv_footer, entity.footer)
    }

    override fun onBindChildViewHolder(holder: BaseViewHolder, groupPosition: Int, childPosition: Int) {
        val entity = getItems()[groupPosition].childList[childPosition]
        holder.setText(R.id.tv_child, entity.child)
    }


}
