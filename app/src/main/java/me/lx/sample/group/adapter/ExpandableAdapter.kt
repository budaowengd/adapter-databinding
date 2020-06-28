package me.lx.sample.group.adapter

import android.widget.ExpandableListView
import androidx.databinding.ViewDataBinding
import me.lx.rv.group.GroupedRecyclerViewAdapter
import me.lx.sample.R
import me.lx.sample.databinding.AdapterExpandableHeaderBinding
import me.lx.sample.group.entity.ChildEntity
import me.lx.sample.group.entity.ExpandableGroupEntity


/**
 * 可展开收起的Adapter。他跟普通的[GroupedListAdapter]基本是一样的。
 * 它只是利用了[GroupedRecyclerViewAdapter]的
 * 删除一组里的所有子项[GroupedRecyclerViewAdapter.notifyChildrenRemoved]} 和
 * 插入一组里的所有子项[GroupedRecyclerViewAdapter.notifyChildrenInserted]
 * 两个方法达到列表的展开和收起的效果。
 * 这种列表类似于[ExpandableListView]的效果。
 * 这里我把列表的组尾去掉是为了效果上更像ExpandableListView。
 *
 * 实现的原理就是重写 getChildrenCount() 返回不同数量
 */
class ExpandableAdapter : GroupedRecyclerViewAdapter<ExpandableGroupEntity, ChildEntity>() {

    override fun getChildrenCount(groupPosition: Int, groupItem: ExpandableGroupEntity): Int {
        //如果当前组收起，就直接返回0，否则才返回子项数。这是实现列表展开和收起的关键。
        if (isExpand(groupPosition)) {
           // return getChildrenList(getItems()[groupPosition]).size
            return groupItem.childList.size
        }
        return 0
    }


    override fun hasHeader(groupPosition: Int): Boolean {
        return true
    }

    override fun hasFooter(groupPosition: Int): Boolean {
        return false
    }

    override fun getHeaderLayout(viewType: Int): Int {
        return R.layout.adapter_expandable_header
    }

    override fun getFooterLayout(viewType: Int): Int {
        return 0
    }

    override fun getChildLayout(viewType: Int): Int {
        return R.layout.adapter_child
    }

    override fun onBindHeaderViewHolder(binding: ViewDataBinding, groupItem: ExpandableGroupEntity, groupPosition: Int) {
        val headerBinding = binding as AdapterExpandableHeaderBinding
        if (groupItem.isExpand) {
            headerBinding.ivState.rotation = 90f
        } else {
            headerBinding.ivState.rotation = 0f
        }
    }

    override fun onBindFooterViewHolder(binding: ViewDataBinding, groupItem: ExpandableGroupEntity, groupPosition: Int) {}
    override fun onBindChildViewHolder(
        binding: ViewDataBinding,
        group: ExpandableGroupEntity,
        child: ChildEntity,
        groupPosition: Int,
        childPosition: Int
    ) {

    }
//    override fun onBindChildViewHolder(
//        binding: ViewDataBinding, groupItem: ExpandableGroupEntity, child: ChildEntity,
//        groupPosition: Int,
//        childPosition: Int
//    ) {
//    }

    /**
     * 判断当前组是否展开
     *
     * @param groupPosition
     * @return
     */
    fun isExpand(groupPosition: Int): Boolean {
        val entity = getGroupList()[groupPosition]
        return entity.isExpand
    }

    /**
     * 展开一个组
     *
     * @param groupPosition
     * @param animate
     */
    @JvmOverloads
    fun expandGroup(item: ExpandableGroupEntity, animate: Boolean = false) {
        println("扩张...Child=" + item.childList.size + "  animate=" + animate)
        item.isExpand = true
        if (animate) {
            notifyChildrenInserted(getGroupPosition(item))
        } else {
            notifyDataChanged()
        }
    }

    /**
     * 收起一个组
     *
     * @param groupPosition
     * @param animate
     */
    @JvmOverloads
    fun collapseGroup(item: ExpandableGroupEntity, animate: Boolean = false) {
        println("收起前..Child=" + item.childList.size + "  animate=" + animate)
        item.isExpand = false
        if (animate) {
            notifyChildrenRemoved(getGroupPosition(item))
        } else {
            notifyDataChanged()
        }
        println("收起后..Child=" + item.childList.size)
    }

    override fun getChildrenList(groupItem: ExpandableGroupEntity): List<ChildEntity> {
        return groupItem.childList
    }




}
