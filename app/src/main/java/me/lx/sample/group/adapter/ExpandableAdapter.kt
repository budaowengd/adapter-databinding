package me.lx.sample.group.adapter

import android.widget.ExpandableListView
import android.widget.ImageView
import me.lx.rv.group.BaseViewHolder
import me.lx.rv.group.GroupedRecyclerViewAdapter
import me.lx.sample.R
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
 */
class ExpandableAdapter : GroupedRecyclerViewAdapter<ExpandableGroupEntity, ChildEntity>() {

    override fun getChildrenCount(groupPosition: Int): Int {
        //如果当前组收起，就直接返回0，否则才返回子项数。这是实现列表展开和收起的关键。
        if (!isExpand(groupPosition)) {
            return 0
        }
        val children = getItems()[groupPosition].childList
        return children.size
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

    override fun onBindHeaderViewHolder(holder: BaseViewHolder, groupPosition: Int) {
        val entity = getItems()[groupPosition]
        holder.setText(R.id.tv_expandable_header, entity.header)
        val ivState = holder.get<ImageView>(R.id.iv_state)
        if (entity.isExpand) {
            ivState.rotation = 90f
        } else {
            ivState.rotation = 0f
        }
    }

    override fun onBindFooterViewHolder(holder: BaseViewHolder, groupPosition: Int) {}

    override fun onBindChildViewHolder(holder: BaseViewHolder, groupPosition: Int, childPosition: Int) {
        val entity = getItems()[groupPosition].childList[childPosition]
        holder.setText(R.id.tv_child, entity.child)
    }

    /**
     * 判断当前组是否展开
     *
     * @param groupPosition
     * @return
     */
    fun isExpand(groupPosition: Int): Boolean {
        val entity = getItems()[groupPosition]
        return entity.isExpand
    }

    /**
     * 展开一个组
     *
     * @param groupPosition
     * @param animate
     */
    @JvmOverloads
    fun expandGroup(groupPosition: Int, animate: Boolean = false) {
        val entity = getItems()[groupPosition]
        entity.isExpand = true
        if (animate) {
            notifyChildrenInserted(groupPosition)
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
    fun collapseGroup(groupPosition: Int, animate: Boolean = false) {
        val entity = getItems()[groupPosition]
        entity.isExpand = false
        if (animate) {
            notifyChildrenRemoved(groupPosition)
        } else {
            notifyDataChanged()
        }
    }

    override fun getChildrenList(group: ExpandableGroupEntity): List<ChildEntity> {
        return group.childList
    }
}
/**
 * 展开一个组
 *
 * @param groupPosition
 */
/**
 * 收起一个组
 *
 * @param groupPosition
 */
