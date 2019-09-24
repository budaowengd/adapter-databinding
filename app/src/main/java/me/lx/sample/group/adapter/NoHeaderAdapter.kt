package me.lx.sample.group.adapter

import me.lx.rv.group.BaseViewHolder

/**
 * 这是不带组头的Adapter。
 * 只需要[GroupedRecyclerViewAdapter.hasHeader]方法返回false就可以去掉组尾了。
 */
class NoHeaderAdapter : GroupedListAdapter() {

    /**
     * 返回false表示没有组头
     *
     * @param groupPosition
     * @return
     */
    override fun hasHeader(groupPosition: Int): Boolean {
        return false
    }

    /**
     * 当hasHeader返回false时，这个方法不会被调用。
     *
     * @return
     */
    override fun getHeaderLayout(viewType: Int): Int {
        return 0
    }

    /**
     * 当hasHeader返回false时，这个方法不会被调用。
     *
     * @param holder
     * @param groupPosition
     */
    override fun onBindHeaderViewHolder(holder: BaseViewHolder, groupPosition: Int) {

    }
}
