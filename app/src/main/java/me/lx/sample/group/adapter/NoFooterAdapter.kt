package me.lx.sample.group.adapter

import androidx.databinding.ViewDataBinding
import me.lx.sample.group.entity.GroupEntity

/**
 * 这是不带组尾的Adapter。
 * 只需要[GroupedRecyclerViewAdapter.hasFooter]方法返回false就可以去掉组尾了。
 */
class NoFooterAdapter : GroupedListAdapter() {

    /**
     * 返回false表示没有组头
     *
     * @param groupPosition
     * @return
     */
    override fun hasFooter(groupPosition: Int): Boolean {
        return false
    }

    /**
     * 当hasHeader返回false时，这个方法不会被调用。
     *
     * @return
     */
    override fun getFooterLayout(viewType: Int): Int {
        return 0
    }

    /**
     * 当 hasFooter 返回false时，这个方法不会被调用。
     *
     * @param holder
     * @param groupPosition
     */
    override fun onBindFooterViewHolder(binding: ViewDataBinding, groupItem: GroupEntity, groupPosition: Int) {

    }
}
