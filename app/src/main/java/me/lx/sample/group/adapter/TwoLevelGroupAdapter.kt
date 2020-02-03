package me.lx.sample.group.adapter

import androidx.databinding.ViewDataBinding
import me.lx.rv.group.TwoLevelGroupedRecyclerViewAdapter
import me.lx.sample.R
import me.lx.sample.group.entity.ChildChildEntity
import me.lx.sample.group.entity.TwoLevelChildEntity
import me.lx.sample.group.entity.TwoLevelGroupEntity

/**
 * 2级分组
 * group1-header
 *  child1
 *      child1-1
 *      child2-2
 *  child2
 *      child2-1
 *      child2-2
 */
open class TwoLevelGroupAdapter :
    TwoLevelGroupedRecyclerViewAdapter<TwoLevelGroupEntity, TwoLevelChildEntity, ChildChildEntity>() {

    // childPosition 是3, 实际上要减去child1 的大小2 . =1

    override fun getChildGroupList(group: TwoLevelGroupEntity): List<TwoLevelChildEntity> {
        return group.childGroupList
    }

    override fun getChildChildList(childGroup: TwoLevelChildEntity): List<ChildChildEntity> {
        return childGroup.childChildList
    }

    override fun hasHeader(groupPosition: Int): Boolean {
        return getItems()[groupPosition].childGroupList.size > 0
    }

    override fun hasFooter(groupPosition: Int): Boolean {
        return getItems()[groupPosition].childGroupList.size > 0
    }

    override fun getHeaderLayout(viewType: Int): Int {
        return R.layout.adapter_header_two_level
    }

    override fun getFooterLayout(viewType: Int): Int {
        return R.layout.adapter_footer_two_level
    }

    override fun getChildLayout(viewType: Int): Int {
        return R.layout.adapter_child_two_level
    }

    override fun getChildChildLayout(viewType: Int): Int {
        return R.layout.adapter_child_child_two_level
    }

    override fun onBindHeaderViewHolder(
        binding: ViewDataBinding,
        group: TwoLevelGroupEntity,
        groupPosition: Int
    ) {

    }

    override fun onBindFooterViewHolder(
        binding: ViewDataBinding,
        group: TwoLevelGroupEntity,
        groupPosition: Int
    ) {
    }

    override fun onBindChildViewHolder(
        binding: ViewDataBinding,
        group: TwoLevelGroupEntity,
        child: TwoLevelChildEntity,
        groupPosition: Int,
        childPosition: Int
    ) {
        // 而外设置变量
    }


}