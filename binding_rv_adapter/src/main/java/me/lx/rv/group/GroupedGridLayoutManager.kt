package me.lx.rv.group

import android.content.Context
import android.util.AttributeSet

import androidx.recyclerview.widget.GridLayoutManager


/**
 * 为分组列表提供的GridLayoutManager。
 * 因为分组列表如果要使用GridLayoutManager实现网格布局。要保证组的头部和尾部是要单独占用一行的。
 * 否则组的头、尾可能会跟子项混着一起，造成布局混乱。
 */
open class GroupedGridLayoutManager : GridLayoutManager {

    private var mAdapter: GroupedRecyclerViewAdapter<*, *>? = null

    constructor(
        context: Context, spanCount: Int,
        adapter: GroupedRecyclerViewAdapter<*, *>
    ) : super(context, spanCount) {
        mAdapter = adapter
        setSpanSizeLookup()
    }

    constructor(
        context: Context, spanCount: Int, orientation: Int,
        reverseLayout: Boolean, adapter: GroupedRecyclerViewAdapter<*, *>
    ) : super(context, spanCount, orientation, reverseLayout) {
        this.mAdapter = adapter
        setSpanSizeLookup()
    }

    constructor(
        context: Context, attrs: AttributeSet, defStyleAttr: Int,
        defStyleRes: Int, adapter: GroupedRecyclerViewAdapter<*, *>
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        this.mAdapter = adapter
        setSpanSizeLookup()
    }

    private fun setSpanSizeLookup() {
        super.setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val count = spanCount
                if (mAdapter != null) {
                    val type = mAdapter!!.judgeType(position)
                    //只对子项做Grid效果
                    if (type == GroupedRecyclerViewAdapter.TYPE_CHILD) {
                        val groupPosition = mAdapter!!.getGroupPositionForPosition(position)
                        val childPosition = mAdapter!!.getChildPositionForPosition(groupPosition, position)
                        return getChildSpanSize(groupPosition, childPosition)
                    }
                }

                return count
            }
        })
    }

    /**
     * 提供这个方法可以使外部改变子项的SpanSize。
     * 这个方法的作用跟[SpanSizeLookup.getSpanSize]一样。
     * @param groupPosition
     * @param childPosition
     * @return
     */
    open fun getChildSpanSize(groupPosition: Int, childPosition: Int): Int {
        return 1
    }

    override fun setSpanSizeLookup(spanSizeLookup: GridLayoutManager.SpanSizeLookup) {

    }
}