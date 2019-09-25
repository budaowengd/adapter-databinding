package me.lx.sample.group.adapter

import me.lx.rv.group.BaseViewHolder
import me.lx.rv.group.GroupedRecyclerViewAdapter
import me.lx.sample.R
import me.lx.sample.group.entity.ChildEntity
import me.lx.sample.group.entity.GroupEntity

/**
 * 头、尾和子项都支持多种类型的Adapter。他跟普通的[GroupedListAdapter]基本是一样的。
 * 只需要重写[GroupedRecyclerViewAdapter]里的三个方法就可以实现头、尾和子项的多种类型。
 * 使用的方式跟普通的RecyclerView实现多种type是一样的。
 * [GroupedRecyclerViewAdapter.getHeaderViewType] 返回Header的viewType
 * [GroupedRecyclerViewAdapter.getFooterViewType]返回Footer的viewType
 * [GroupedRecyclerViewAdapter.getChildViewType]返回Child的viewType
 */
open class VariousAdapter : GroupedRecyclerViewAdapter<GroupEntity, ChildEntity>() {


    override fun getChildrenList(group: GroupEntity): List<ChildEntity> {
        return group.childList
    }


    override fun getChildrenCount(groupPosition: Int): Int {
        val children = getItems()[groupPosition].childList
        return children?.size ?: 0
    }

    override fun hasHeader(groupPosition: Int): Boolean {
        return true
    }

    override fun hasFooter(groupPosition: Int): Boolean {
        return true
    }

    override fun getHeaderLayout(viewType: Int): Int {
        return if (viewType == TYPE_HEADER_1) {
            R.layout.adapter_header
        } else {
            R.layout.adapter_header_2
        }
    }

    override fun getFooterLayout(viewType: Int): Int {
        return if (viewType == TYPE_FOOTER_1) {
            R.layout.adapter_footer
        } else {
            R.layout.adapter_footer_2
        }
    }

    override fun getChildLayout(viewType: Int): Int {
        return if (viewType == TYPE_CHILD_1) {
            R.layout.adapter_child
        } else {
            R.layout.adapter_child_2
        }
    }

    override fun onBindHeaderViewHolder(holder: BaseViewHolder, groupPosition: Int) {
        val entity = getItems()[groupPosition]
        val viewType = getHeaderViewType(groupPosition)
        if (viewType == TYPE_HEADER_1) {
            holder.setText(R.id.tv_header, "第一种头部：" + entity.header)
        } else if (viewType == TYPE_HEADER_2) {
            holder.setText(R.id.tv_header_2, "第二种头部：" + entity.header)
        }
    }

    override fun onBindFooterViewHolder(holder: BaseViewHolder, groupPosition: Int) {
        val entity = getItems()[groupPosition]
        val viewType = getFooterViewType(groupPosition)
        if (viewType == TYPE_FOOTER_1) {
            holder.setText(R.id.tv_footer, "第一种尾部：" + entity.footer)
        } else if (viewType == TYPE_FOOTER_2) {
            holder.setText(R.id.tv_footer_2, "第二种尾部：" + entity.footer)
        }
    }

    override fun onBindChildViewHolder(holder: BaseViewHolder, groupPosition: Int, childPosition: Int) {
        val entity = getItems()[groupPosition].childList[childPosition]
        val viewType = getChildViewType(groupPosition, childPosition)
        if (viewType == TYPE_CHILD_1) {
            holder.setText(R.id.tv_child, "第一种子项：" + entity.child)
        } else if (viewType == TYPE_CHILD_2) {
            holder.setText(R.id.tv_child_2, "第二种子项：" + entity.child)
        }
    }

    override fun getHeaderViewType(groupPosition: Int): Int {
        return if (groupPosition % 2 == 0) {
            TYPE_HEADER_1
        } else {
            TYPE_HEADER_2
        }
    }

    override fun getFooterViewType(groupPosition: Int): Int {
        return if (groupPosition % 2 == 0) {
            TYPE_FOOTER_1
        } else {
            TYPE_FOOTER_2
        }
    }

    override fun getChildViewType(groupPosition: Int, childPosition: Int): Int {
        return if (groupPosition % 2 == 0) {
            TYPE_CHILD_1
        } else {
            TYPE_CHILD_2
        }
    }

    companion object {
        private val TYPE_HEADER_1 = 1
        private val TYPE_HEADER_2 = 2
        private val TYPE_FOOTER_1 = 3
        private val TYPE_FOOTER_2 = 4
        private val TYPE_CHILD_1 = 5
        private val TYPE_CHILD_2 = 6
    }


}
