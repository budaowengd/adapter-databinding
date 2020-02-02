package me.lx.sample.group.adapter

import androidx.databinding.ViewDataBinding
import me.lx.rv.group.TwoLevelGroupedRecyclerViewAdapter
import me.lx.sample.R
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
open class TwoLevelGroupAdapter : TwoLevelGroupedRecyclerViewAdapter<TwoLevelGroupEntity, TwoLevelChildEntity>() {

    override fun getChildrenList(groupItem: TwoLevelGroupEntity): List<TwoLevelChildEntity> {
        return groupItem.childList
    }

    override fun getChildrenCount(groupPosition: Int, groupItem: TwoLevelGroupEntity): Int {
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

    override fun getChildChildLayout(viewType: Int): Int {
        return R.layout.adapter_child
    }

    override fun onBindHeaderViewHolder(binding: ViewDataBinding, groupItem: TwoLevelGroupEntity, groupPosition: Int) {
    }

    override fun onBindFooterViewHolder(binding:ViewDataBinding,  groupItem: TwoLevelGroupEntity,groupPosition: Int) {
    }

    override fun onBindChildViewHolder(binding:ViewDataBinding, groupItem: TwoLevelGroupEntity, childItem: TwoLevelChildEntity,groupPosition: Int,childPosition: Int) {
        // 而外设置变量
    }




}
