package me.lx.rv.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.lx.rv.*
import me.lx.rv.click.ClickListener
import me.lx.rv.tools.Ls
import java.util.*

/**
 * 通用的分组列表Adapter。通过它可以很方便的实现列表的分组效果。
 * 这个类提供了一系列的对列表的更新、删除和插入等操作的方法。
 * 使用者要使用这些方法的列表进行操作，而不要直接使用RecyclerView.Adapter的方法。
 * 因为当分组列表发生变化时，需要及时更新分组列表的组结构[GroupedRecyclerViewAdapter.mStructures]
 *
 * 1、T泛型: 组对象
 * 2、C泛型: 组里的孩子
 */
abstract class GroupedRecyclerViewAdapter<G, C> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), BindingCollectionAdapter<G> {
    companion object {
        const val TAG = "GroupedAdapter"

        @JvmField
        val DEBUG = BuildConfig.DEBUG

        @JvmField
        var TYPE_HEADER = R.integer.type_header

        @JvmField
        val TYPE_FOOTER = R.integer.type_footer

        @JvmField
        val TYPE_CHILD = R.integer.type_child
    }

    //    private var mOnHeaderClickListener: OnHeaderClickListener? = null
//    private var mOnFooterClickListener: OnFooterClickListener? = null
//    private var mOnChildClickListener: OnChildClickListener? = null

    private var mClickChildListener: ClickListener? = null
    private var mClickHeaderListener: ClickListener? = null
    private var mClickFooterListener: ClickListener? = null

    //保存分组列表的组结构
    protected var mStructures = ArrayList<GroupStructure>()

    //数据是否发生变化。如果数据发生变化，要及时更新组结构。
    public var isDataChanged: Boolean = false
    private var mTempPosition: Int = 0

    private var inflater: LayoutInflater? = null

    private var callback: WeakReferenceOnListChangedCallback<G>? = null
    private var groupList: List<G>? = null
    private var recyclerView: RecyclerView? = null

    // 当孩子为空的时候,如果存在header,就自动移除header
    private var childEmptyIsRemoveHeader: Boolean? = null

    //    constructor(dataList: List<G>) : super() {
//        groupList = dataList
//    }
    init {
        initData()
    }

    private fun initData() {
        registerAdapterDataObserver(GroupDataObserver())
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (groupList is ObservableList<G>) {
            callback = WeakReferenceOnListChangedCallback<G>(
                recyclerView,
                this,
                groupList!! as ObservableList<G>
            )
            (groupList as ObservableList<G>).addOnListChangedCallback(callback)
        }
        this.recyclerView = recyclerView
        structureChanged()
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)

        //处理StaggeredGridLayout，保证组头和组尾占满一行。
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder, holder.layoutPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater!!,
            getLayoutId(mTempPosition, viewType),
            parent,
            false
        )
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = judgeType(position)
        val groupPosition = getGroupPositionForPosition(position)
        val binding = DataBindingUtil.getBinding<ViewDataBinding>(holder.itemView)!!
        val group = groupList!![groupPosition]
        when (type) {
            TYPE_HEADER -> {
                binding.setVariable(BR.gHeader, group)
                if (mClickHeaderListener != null) {
                    binding.setVariable(BR.gHeaderClick, mClickHeaderListener)
                }
                onBindHeaderViewHolder(binding, group, groupPosition)
            }
            TYPE_FOOTER -> {
                binding.setVariable(BR.gFooter, group)
                if (mClickFooterListener != null) {
                    binding.setVariable(BR.gFooterClick, mClickFooterListener)
                }
                onBindFooterViewHolder(binding, group, groupPosition)
            }
            TYPE_CHILD -> {
                val childPosition = getChildPositionForPosition(groupPosition, position)
                val child = getChildrenList(group)[childPosition]
                binding.setVariable(BR.c, child)
                if (mClickChildListener != null) {
                    binding.setVariable(BR.cClick, mClickChildListener)
                }
                onBindChildViewHolder(binding, group, child, groupPosition, childPosition)
            }
        }
        binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        if (isDataChanged) {
            structureChanged()
        }
        val count= count()
      //  Ls.d("getItemCount()...count=$count")
        return count
    }

    override fun getItemViewType(position: Int): Int {
       // Ls.d("getItemViewType().111111..position=$position")
        mTempPosition = position
        val groupPosition = getGroupPositionForPosition(position)
        val type = judgeType(position)
        if (type == TYPE_HEADER) {
            return getHeaderViewType(groupPosition)
        } else if (type == TYPE_FOOTER) {
            return getFooterViewType(groupPosition)
        } else if (type == TYPE_CHILD) {
            val childPosition = getChildPositionForPosition(groupPosition, position)
            return getChildViewType(groupPosition, childPosition)
        }
        return super.getItemViewType(position)
    }

    open fun setGroupList(groupList: List<G>) {
        if (System.identityHashCode(groupList) == System.identityHashCode(this.groupList)) {
            return
        }
        if (recyclerView != null) {
            if (this.groupList is ObservableList<G>) {
                (this.groupList as ObservableList<G>).removeOnListChangedCallback(callback)
                callback = null
            }
            if (groupList is ObservableList<G>) {
                callback = WeakReferenceOnListChangedCallback(recyclerView!!, this, groupList)
                groupList.addOnListChangedCallback(callback)
            }
        }
        this.groupList = groupList
        registerChildListChangedCallback(groupList)
        notifyDataSetChanged()
    }

    /**
     * 给每一组里的 childList的添加数据改变监听
     */
    private fun registerChildListChangedCallback(groupList: List<G>) {
        // 为每组的childList添加监听
        for (group in groupList) {
            addChildListChangedCallbackByGroup(group)
        }
    }

    open fun addChildListChangedCallbackByGroup(group: G) {
        val childrenList = getChildrenList(group)
        if (childrenList is ObservableList) {
            val childListCallback = ChildListChangedCallback<G, C>(this, childEmptyIsRemoveHeader)
            childrenList.addOnListChangedCallback(childListCallback as ObservableList.OnListChangedCallback<Nothing>)
        }
    }

    fun getGroupList(): List<G> {
        return this.groupList!!
    }


    private fun isStaggeredGridLayout(holder: RecyclerView.ViewHolder): Boolean {
        val layoutParams = holder.itemView.layoutParams
        return layoutParams is StaggeredGridLayoutManager.LayoutParams
    }

    private fun handleLayoutIfStaggeredGridLayout(holder: RecyclerView.ViewHolder, position: Int) {
        if (judgeType(position) == TYPE_HEADER || judgeType(position) == TYPE_FOOTER) {
            val p = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            p.isFullSpan = true
        }
    }


    open fun getHeaderViewType(groupPosition: Int): Int {
        return TYPE_HEADER
    }

    open fun getFooterViewType(groupPosition: Int): Int {
        return TYPE_FOOTER
    }

    open fun getChildViewType(groupPosition: Int, childPosition: Int): Int {
        return TYPE_CHILD
    }

    private fun getLayoutId(position: Int, viewType: Int): Int {
        val type = judgeType(position)
        if (type == TYPE_HEADER) {
            return getHeaderLayout(viewType)
        } else if (type == TYPE_FOOTER) {
            return getFooterLayout(viewType)
        } else if (type == TYPE_CHILD) {
            return getChildLayout(viewType)
        }
        return 0
    }

    private fun count(): Int {
        return countGroupRangeItem(0, mStructures.size)
    }

    /**
     * 判断item的type 头部 尾部 和 子项
     *
     * @param position
     * @return
     */
    fun judgeType(position: Int): Int {
        var itemCount = 0
        val groupCount = mStructures.size

        for (i in 0 until groupCount) {
            val structure = mStructures[i]
            if (structure.hasHeader()) {
                itemCount += 1
                if (position < itemCount) {
                    return TYPE_HEADER
                }
            }

            itemCount += structure.childrenCount
            if (position < itemCount) {
                return TYPE_CHILD
            }

            if (structure.hasFooter()) {
                itemCount += 1
                if (position < itemCount) {
                    return TYPE_FOOTER
                }
            }
        }

        throw IndexOutOfBoundsException(
            "can't determine the item type of the position." +
                    "position = " + position + ",item count = " + getItemCount()
        )
    }

    /**
     * 重置组结构列表
     */
    private fun structureChanged() {
        mStructures.clear()
        val groupCount = getGroupCount()
        for (i in 0 until groupCount) {
            mStructures.add(GroupStructure(hasHeader(i), hasFooter(i), getSelfChildrenCount(i)))
        }
        isDataChanged = false
       // if (DEBUG) {
      //      Ls.d("重置组结构列表...666666...size=${mStructures.size}")
       // }
    }

    fun getGroupCount(): Int {
        return groupList!!.size
    }

    fun getGroupPosition(group: G): Int {
        var groupPosition = -1
        run breaking@{
            groupList!!.forEachIndexed { index, group ->
                if (group == group) {
                    groupPosition = index
                    return@breaking
                }
            }
        }
        return groupPosition
    }

    /**
     * 根据每组里面的 childList 计算所在的组（groupPosition）
     *
     * @return 组下标 groupPosition
     */
    fun getGroupPositionByChildList(childList: List<C>): Int {
        var groupIndex = -1
        val childListHashCode = System.identityHashCode(childList)
        run breaking@{
            groupList!!.forEachIndexed { index, group ->
                if (System.identityHashCode(getChildrenList(group)) == childListHashCode) {
                    groupIndex = index
                    return@breaking
                }
            }
        }
        return groupIndex
    }

    /**
     * 根据下标计算position所在的组（groupPosition）
     *
     * @param position 下标
     * @return 组下标 groupPosition
     */
    fun getGroupPositionForPosition(position: Int): Int {
        var count = 0
        val groupCount = mStructures.size
        for (i in 0 until groupCount) {
            count += countGroupItem(i)
            if (position < count) {
                return i
            }
        }
        return -1
    }

    /**
     * 根据下标计算position在组中位置（childPosition）
     *
     * @param groupPosition 所在的组
     * @param position      下标
     * @return 子项下标 childPosition
     */
    fun getChildPositionForPosition(groupPosition: Int, position: Int): Int {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val itemCount = countGroupRangeItem(0, groupPosition + 1)
            val structure = mStructures[groupPosition]
            val p =
                structure.childrenCount - (itemCount - position) + if (structure.hasFooter()) 1 else 0
            if (p >= 0) {
                return p
            }
        }
        return -1
    }

    /**
     * 获取一个组的组头下标 如果该组没有组头 返回-1
     *
     * @param groupPosition 组下标
     * @return 下标
     */
    fun getPositionForGroupHeader(groupPosition: Int): Int {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val structure = mStructures[groupPosition]
            if (!structure.hasHeader()) {
                //return if (!hasHeader(groupPosition)) {
                return -1
            } else {
                return countGroupRangeItem(0, groupPosition)
            }
        }
        return -1
    }

    /**
     * 获取一个组的组尾下标 如果该组没有组尾 返回-1
     *
     * @param groupPosition 组下标
     * @return 下标
     */
    fun getPositionForGroupFooter(groupPosition: Int): Int {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val structure = mStructures[groupPosition]
            return if (!structure.hasFooter()) {
                -1
            } else countGroupRangeItem(0, groupPosition + 1) - 1
        }
        return -1
    }

    /**
     * 获取一个组指定的子项下标 如果没有 返回-1
     *
     * @param groupPosition 组下标
     * @param childPosition 子项的组内下标
     * @return 下标
     */
    fun getPositionForChild(groupPosition: Int, childPosition: Int): Int {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val structure = mStructures[groupPosition]
            if (structure.childrenCount > childPosition) {
                val itemCount = countGroupRangeItem(0, groupPosition)
                return itemCount + childPosition + if (structure.hasHeader()) 1 else 0
            }
        }
        return -1
    }

    /**
     * 计算一个组里有多少个Item（头加尾加子项）
     *
     * @param groupPosition
     * @return
     */
    fun countGroupItem(groupPosition: Int): Int {
        var itemCount = 0
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val structure = mStructures[groupPosition]
            if (structure.hasHeader()) {
                itemCount += 1
            }
            itemCount += structure.childrenCount
            if (structure.hasFooter()) {
                itemCount += 1
            }
        }
        return itemCount
    }

    /**
     * 计算多个组的项的总和
     *
     * @return
     */
    fun countGroupRangeItem(start: Int, count: Int): Int {
        var itemCount = 0
        val size = mStructures.size
        var i = start
        while (i < size && i < start + count) {
            itemCount += countGroupItem(i)
            i++
        }
        return itemCount
    }

    //****** 刷新操作 *****//


    /**
     * 通知数据列表刷新
     */
    fun notifyDataChanged() {
        isDataChanged = true
        notifyDataSetChanged()
    }


    /**
     * 通知一组数据刷新，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    fun notifyGroupChanged(groupPosition: Int) {
        val index = getPositionForGroupHeader(groupPosition)
        val itemCount = countGroupItem(groupPosition)
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeChanged(index, itemCount)
        }
    }


    /**
     * 通知多组数据刷新，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    fun notifyGroupRangeChanged(groupPosition: Int, count: Int) {
        val index = getPositionForGroupHeader(groupPosition)
        var itemCount = 0
        if (groupPosition + count <= mStructures.size) {
            itemCount = countGroupRangeItem(groupPosition, groupPosition + count)
        } else {
            itemCount = countGroupRangeItem(groupPosition, mStructures.size)
        }
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeChanged(index, itemCount)
        }
    }


    /**
     * 通知组头刷新
     *
     * @param groupPosition
     */
    fun notifyHeaderChanged(groupPosition: Int) {
        val index = getPositionForGroupHeader(groupPosition)
        if (index >= 0) {
            notifyItemChanged(index)
        }
    }


    /**
     * 通知组尾刷新
     *
     * @param groupPosition
     */
    fun notifyFooterChanged(groupPosition: Int) {
        val index = getPositionForGroupFooter(groupPosition)
        if (index >= 0) {
            notifyItemChanged(index)
        }
    }


    /**
     * 通知一组里的某个子项刷新
     *
     * @param groupPosition
     * @param childPosition
     */
    fun notifyChildChanged(groupPosition: Int, childPosition: Int) {
        val index = getPositionForChild(groupPosition, childPosition)
        if (index >= 0) {
            notifyItemChanged(index)
        }
    }


    /**
     * 通知一组里的多个子项刷新
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    fun notifyChildRangeChanged(groupPosition: Int, childPosition: Int, count: Int) {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val index = getPositionForChild(groupPosition, childPosition)
            if (index >= 0) {
                val structure = mStructures[groupPosition]
                if (structure.childrenCount >= childPosition + count) {
                    notifyItemRangeChanged(index, count)
                } else {
                    notifyItemRangeChanged(index, structure.childrenCount - childPosition)
                }
            }
        }
    }


    /**
     * 通知一组里的所有子项刷新
     *
     * @param groupPosition
     */
    fun notifyChildrenChanged(groupPosition: Int) {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val index = getPositionForChild(groupPosition, 0)
            if (index >= 0) {
                val structure = mStructures[groupPosition]
                notifyItemRangeChanged(index, structure.childrenCount)
            }
        }
    }

    //****** 删除操作 *****//


    /**
     * 通知所有数据删除
     */
    fun notifyDataRemoved() {
        notifyItemRangeRemoved(0, itemCount)
        mStructures.clear()
    }


    /**
     * 通知一组数据删除，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    fun notifyGroupRemoved(groupPosition: Int) {
        val index = getPositionForGroupHeader(groupPosition)
        val itemCount = countGroupItem(groupPosition)
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeRemoved(index, itemCount)
            notifyItemRangeChanged(index, getItemCount() - itemCount)
            mStructures.removeAt(groupPosition)
        }
    }


    /**
     * 通知多组数据删除，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    fun notifyGroupRangeRemoved(groupPosition: Int, count: Int) {
        val index = getPositionForGroupHeader(groupPosition)
        var itemCount = 0
        if (groupPosition + count <= mStructures.size) {
            itemCount = countGroupRangeItem(groupPosition, groupPosition + count)
        } else {
            itemCount = countGroupRangeItem(groupPosition, mStructures.size)
        }
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeRemoved(index, itemCount)
            notifyItemRangeChanged(index, getItemCount() - itemCount)
            // mStructures.removeAt(groupPosition)
        }
    }


    /**
     * 通知组头删除
     *
     * @param groupPosition
     */
    fun notifyHeaderRemoved(groupPosition: Int) {
        val index = getPositionForGroupHeader(groupPosition)
        if (index >= 0) {
            val structure = mStructures[groupPosition]
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, itemCount - index)
            structure.setHasHeader(false)
        }
    }


    /**
     * 通知组尾删除
     *
     * @param groupPosition
     */
    fun notifyFooterRemoved(groupPosition: Int) {
        val index = getPositionForGroupFooter(groupPosition)
        if (index >= 0) {
            val structure = mStructures[groupPosition]
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, itemCount - index)
            structure.setHasFooter(false)
        }
    }


    /**
     * 通知一组里的某个子项删除
     *
     * @param groupPosition
     * @param childPosition
     */
    fun notifyChildRemoved(groupPosition: Int, childPosition: Int) {
        val index = getPositionForChild(groupPosition, childPosition)
        if (index >= 0) {
            val structure = mStructures[groupPosition]
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, itemCount - index)
            structure.childrenCount = structure.childrenCount - 1
        }
    }


    /**
     * 通知一组里的多个子项删除
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    fun notifyChildRangeRemoved(groupPosition: Int, childPosition: Int, count: Int) {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val index = getPositionForChild(groupPosition, childPosition)
            if (index >= 0) {
                val structure = mStructures[groupPosition]
                val childCount = structure.childrenCount
                var removeCount = count
                if (childCount < childPosition + count) {
                    removeCount = childCount - childPosition
                }
                // Ls.d("notifyChildRangeRemoved()..index=$index  itemCount=$itemCount  childCount=$childCount  removeCount=$removeCount  ")
                notifyItemRangeRemoved(index, removeCount)
                notifyItemRangeChanged(index, itemCount - removeCount)
                structure.childrenCount = childCount - removeCount
            }
        }
    }


    /**
     * 通知一组里的所有子项删除
     *
     * @param groupPosition
     */
    fun notifyChildrenRemoved(groupPosition: Int) {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val index = getPositionForChild(groupPosition, 0)
            if (index >= 0) {
                val structure = mStructures[groupPosition]
                val itemCount = structure.childrenCount
                notifyItemRangeRemoved(index, itemCount)
                notifyItemRangeChanged(index, getItemCount() - itemCount)
                structure.childrenCount = 0
            }
        }
    }

    //****** 插入操作 *****//


    /**
     * 通知一组数据插入
     *
     * @param groupPosition
     */
    fun notifyGroupInserted(groupPosition: Int) {
        var groupPos = groupPosition
        val structure = GroupStructure(
            hasHeader(groupPos),
            hasFooter(groupPos), getSelfChildrenCount(groupPos)
        )
        if (groupPos >= 0 && groupPos < mStructures.size) {
            mStructures.add(groupPos, structure)
        } else {
            mStructures.add(structure)
            groupPos = mStructures.size - 1
        }

        val index = countGroupRangeItem(0, groupPos)
        val itemCount = countGroupItem(groupPos)
        if (itemCount > 0) {
            notifyItemRangeInserted(index, itemCount)
            notifyItemRangeChanged(index + itemCount, getItemCount() - index)
        }
    }


    /**
     * 通知多组数据插入
     *
     * @param groupPosition
     * @param count
     */
    fun notifyGroupRangeInserted(groupPos: Int, count: Int) {
        var groupPosition = groupPos
        val list = ArrayList<GroupStructure>()
        for (i in 0 until count) {
            val structure = GroupStructure(
                hasHeader(i),
                hasFooter(i), getSelfChildrenCount(i)
            )
            list.add(structure)
        }

        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            mStructures.addAll(groupPosition, list)
        } else {
            mStructures.addAll(list)
            groupPosition = mStructures.size - list.size
        }

        val index = countGroupRangeItem(0, groupPosition)
        val itemCount = countGroupRangeItem(groupPosition, count)
        if (itemCount > 0) {
            notifyItemRangeInserted(index, itemCount)
            notifyItemRangeChanged(index + itemCount, getItemCount() - index)
        }
    }


    /**
     * 通知组头插入
     *
     * @param groupPosition
     */
    fun notifyHeaderInserted(groupPosition: Int) {
        if (groupPosition >= 0 && groupPosition < mStructures.size && 0 > getPositionForGroupHeader(
                groupPosition
            )
        ) {
            val structure = mStructures[groupPosition]
            structure.setHasHeader(true)
            val index = countGroupRangeItem(0, groupPosition)
            notifyItemInserted(index)
            notifyItemRangeChanged(index + 1, itemCount - index)
        }
    }


    /**
     * 通知组尾插入
     *
     * @param groupPosition
     */
    fun notifyFooterInserted(groupPosition: Int) {
        if (groupPosition >= 0 && groupPosition < mStructures.size && 0 > getPositionForGroupFooter(
                groupPosition
            )
        ) {
            val structure = mStructures[groupPosition]
            structure.setHasFooter(true)
            val index = countGroupRangeItem(0, groupPosition + 1)
            notifyItemInserted(index)
            notifyItemRangeChanged(index + 1, itemCount - index)
        }
    }


    /**
     * 通知一个子项到组里插入
     *
     * @param groupPosition
     * @param childPosition
     */
    fun notifyChildInserted(groupPosition: Int, childPosition: Int) {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val structure = mStructures[groupPosition]
            var index = getPositionForChild(groupPosition, childPosition)
            if (index < 0) {
                index = countGroupRangeItem(0, groupPosition)
                index += if (structure.hasHeader()) 1 else 0
                index += structure.childrenCount
            }
            structure.childrenCount = structure.childrenCount + 1
            notifyItemInserted(index)
            notifyItemRangeChanged(index + 1, itemCount - index)
        }
    }


    /**
     * 通知一组里的多个子项插入
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    fun notifyChildRangeInserted(groupPosition: Int, childPosition: Int, count: Int) {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            var index = countGroupRangeItem(0, groupPosition)
            val structure = mStructures[groupPosition]
            if (structure.hasHeader()) {
                index++
            }
            if (childPosition < structure.childrenCount) {
                index += childPosition
            } else {
                index += structure.childrenCount
            }
            if (count > 0) {
                structure.childrenCount = structure.childrenCount + count
                notifyItemRangeInserted(index, count)
                notifyItemRangeChanged(index + count, itemCount - index)
            }
        }
    }


    /**
     * 通知一组里的所有子项插入
     *
     * @param groupPosition
     */
    fun notifyChildrenInserted(groupPosition: Int) {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            var index = countGroupRangeItem(0, groupPosition)
            val structure = mStructures[groupPosition]
            if (structure.hasHeader()) {
                index++
            }
            val itemCount = getSelfChildrenCount(groupPosition)
            if (itemCount > 0) {
                structure.childrenCount = itemCount
                notifyItemRangeInserted(index, itemCount)
                notifyItemRangeChanged(index + itemCount, getItemCount() - index)
            }
        }
    }

    //****** 设置点击事件 *****//

    /**
     * 设置组头点击事件
     *
     * @param listener
     */
//    fun setOnHeaderClickListener(listener: OnHeaderClickListener) {
//        mOnHeaderClickListener = listener
//    }

    /**
     * 设置组尾点击事件
     *
     * @param listener
     */
//    fun setOnFooterClickListener(listener: OnFooterClickListener) {
//        mOnFooterClickListener = listener
//    }

    /**
     * 设置子项点击事件
     *
     * @param listener
     */
//    fun setOnChildClickListener(listener: OnChildClickListener) {
//        mOnChildClickListener = listener
//    }
    fun setClickChildListener(listener: ClickListener) {
        mClickChildListener = listener
    }

    fun setClickHeaderListener(listener: ClickListener) {
        mClickHeaderListener = listener
    }

    fun setClickFooterListener(listener: ClickListener) {
        mClickFooterListener = listener
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
    }

    override fun setItemBinding(itemBinding: XmlItemBinding<G>) {

    }

    override fun getItemXmlObj(): XmlItemBinding<G>? {
        return null
    }

    override fun setItems(items: List<G>) {

    }

    override fun getAdapterItem(position: Int): G? {
        return null
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        layoutRes: Int,
        viewGroup: ViewGroup
    ): ViewDataBinding? {
        return null
    }

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: G
    ) {

    }


    fun setChildEmptyRemoveHeader(isRemoveHeaderWhenChildEmpty: Boolean = true): GroupedRecyclerViewAdapter<G, C> {
        childEmptyIsRemoveHeader = isRemoveHeaderWhenChildEmpty
        return this
    }

    private fun getSelfChildrenCount(groupPosition: Int): Int {
        return getChildrenCount(groupPosition, groupList!![groupPosition]!!)
    }

    open fun getFooterLayout(viewType: Int): Int {
        return -1
    }

    open fun hasFooter(groupPosition: Int): Boolean {
        return false
    }

    open fun getChildrenCount(groupPosition: Int, group: G): Int {
        return getChildrenList(group).size
    }

    abstract fun getHeaderLayout(viewType: Int): Int

    abstract fun getChildLayout(viewType: Int): Int

    abstract fun hasHeader(groupPosition: Int): Boolean


    abstract fun getChildrenList(group: G): List<C>

    open fun onBindHeaderViewHolder(vBinding: ViewDataBinding, group: G, groupPosition: Int){

    }

    open fun onBindFooterViewHolder(vBinding: ViewDataBinding, group: G, groupPosition: Int) {}

    open fun onBindChildViewHolder(
        binding: ViewDataBinding,
        group: G, child: C,
        groupPosition: Int,
        childPosition: Int
    ){

    }

    internal inner class GroupDataObserver : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            isDataChanged = true
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            isDataChanged = true
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            onItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            isDataChanged = true
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            isDataChanged = true
        }
    }

    class WeakReferenceOnListChangedCallback<G> constructor(
        var recyclerView: RecyclerView,
        private var groupAdapter: GroupedRecyclerViewAdapter<G, *>,
        groupList: ObservableList<G>
    ) : ObservableList.OnListChangedCallback<ObservableList<G>>() {
        // internal val adapterRef: WeakReference<GroupedRecyclerViewAdapter<G, *>> = AdapterReferenceCollector.createRef
        // (groupAdapter, groupList, this)

        override fun onChanged(sender: ObservableList<G>) {
            if (DEBUG) {
                Ls.d("GroupedRecyclerViewAdapter()....onChanged()...1111..")
            }
            groupAdapter.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(
            sender: ObservableList<G>,
            positionStart: Int,
            itemCount: Int
        ) {
            if (DEBUG) {
                Ls.d("GroupedRecyclerViewAdapter()....onItemRangeChanged()...2222..positionStart=$positionStart  itemCount=$itemCount")
            }
            groupAdapter.notifyGroupRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeInserted(
            sender: ObservableList<G>,
            positionStart: Int,
            itemCount: Int
        ) {
            if (DEBUG) {
                Ls.d("GroupedRecyclerViewAdapter()..onItemRangeInserted()...33333...sender=${sender.size}  itemCount=$itemCount")
            }
            groupAdapter.notifyGroupRangeInserted(positionStart, itemCount)
            for (index in 0 until itemCount) {
//                if(positionStart+index<sender.size){}
                groupAdapter.addChildListChangedCallbackByGroup(sender[positionStart + index])
            }
        }

        override fun onItemRangeMoved(
            sender: ObservableList<G>,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            if (DEBUG) {
                Ls.d("GroupedRecyclerViewAdapter()....onItemRangeMoved()..444.. itemCount=$itemCount")
            }
            groupAdapter.notifyGroupRangeRemoved(fromPosition, itemCount)
        }

        // clear() 会执行这
        override fun onItemRangeRemoved(
            sender: ObservableList<G>,
            positionStart: Int,
            itemCount: Int
        ) {
            if (DEBUG) {
                Ls.d("GroupedRecyclerViewAdapter()....onItemRangeRemoved()...555...positionStart=$positionStart itemCount=$itemCount")
            }
            //groupAdapter.notifyGroupRangeChanged(positionStart, itemCount)
            groupAdapter.notifyGroupRangeRemoved(positionStart, itemCount)
        }
    }


    //    interface OnHeaderClickListener {
//        fun onHeaderClick(
//            adapter: GroupedRecyclerViewAdapter<*, *>,
//            binding:ViewDataBinding,
//            groupPosition: Int
//        )
//    }
//
//    interface OnFooterClickListener {
//        fun onFooterClick(
//            adapter: GroupedRecyclerViewAdapter<*, *>,
//            binding:ViewDataBinding,
//            groupPosition: Int
//        )
//    }
//
//    interface OnChildClickListener {
//        fun onChildClick(
//            adapter: GroupedRecyclerViewAdapter<*, *>, binding:ViewDataBinding,
//            groupPosition: Int, childPosition: Int
//        )
//    }
    private class BindingViewHolder internal constructor(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root)


}