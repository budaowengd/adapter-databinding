package me.lx.sample.group.adapter

import androidx.databinding.ViewDataBinding
import me.lx.rv.group.BaseFun1ClickGroupListener
import me.lx.rv.group.TwoLevelGroupedRecyclerViewAdapter
import me.lx.sample.BR
import me.lx.sample.R
import me.lx.sample.group.entity.ChildChildEntity
import me.lx.sample.group.entity.ChildGroupEntity
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
    TwoLevelGroupedRecyclerViewAdapter<TwoLevelGroupEntity, ChildGroupEntity, ChildChildEntity>() {

    var childGroupFooterClickEvent: BaseFun1ClickGroupListener<ChildGroupEntity>? = null
    // childPosition 是3, 实际上要减去child1 的大小2 . =1

    override fun getChildGroupList(group: TwoLevelGroupEntity): List<ChildGroupEntity> {
        return group.childGroupList
    }

    override fun getChildChildList(childGroup: ChildGroupEntity): List<ChildChildEntity> {
        return childGroup.childChildList
    }

    override fun hasHeader(groupPosition: Int): Boolean {
        //return getItems()[groupPosition].childGroupList.size > 0
        return true
    }

    override fun hasFooter(groupPosition: Int): Boolean {
        //  Ls.d("hasFooter()..111111..groupPosition=$groupPosition")
        if (groupPosition < 2) return false
        return getGroup(groupPosition).childGroupList.size > 0
    }

    override fun hasChildGroupFooter(groupPosition: Int): Boolean {
        return 3 == 2
        // return groupPosition < 1
    }

    override fun hasChildGroupHeader(groupPosition: Int): Boolean {
        return groupPosition % 2 != 0
    }


    override fun getHeaderLayout(viewType: Int): Int {
        return R.layout.adapter_header_two_level
    }

    override fun getFooterLayout(viewType: Int): Int {
        return R.layout.adapter_footer_two_level
    }

    override fun getChildGroupHeaderLayout(viewType: Int): Int {
        return R.layout.adapter_child_two_level
    }

    override fun getChildGroupFooterLayout(viewType: Int): Int {
        return R.layout.adapter_child_group_footer
    }

    override fun getChildChildLayout(viewType: Int): Int {
        return R.layout.adapter_child_child_two_level
    }

    override fun onBindChildGroupFooterViewHolder(
        binding: ViewDataBinding,
        group: TwoLevelGroupEntity,
        childGroup: ChildGroupEntity?,
        groupPosition: Int
    ) {
        binding.setVariable(BR.childGroupFooterClick, childGroupFooterClickEvent)
        binding.setVariable(BR.childGroup, childGroup)
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

    override fun onBindChildGroupHeader(
        binding: ViewDataBinding,
        group: TwoLevelGroupEntity,
        child: ChildGroupEntity,
        groupPosition: Int,
        childPosition: Int
    ) {
        // 而外设置变量
    }




}
