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
import java.lang.NullPointerException

/**
 * 通用的分组列表Adapter。通过它可以很方便的实现列表的分组效果。
 * 这个类提供了一系列的对列表的更新、删除和插入等操作的方法。
 * 使用者要使用这些方法的列表进行操作，而不要直接使用RecyclerView.Adapter的方法。
 * 因为当分组列表发生变化时，需要及时更新分组列表的组结构[Group3RecyclerViewAdapter.mStructures]
 *
 * 1、T泛型: 组对象
 * 2、C泛型: 组里的孩子
 * 3级分组
 * group1-header
 *  child1
 *      child1-1
 *      child2-2
 *  child2
 *      child2-1
 *      child2-2
 *      child2-3
 *  group1-footer
 */
abstract class Group3RecyclerViewAdapter<G, CG, CC> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), BindingCollectionAdapter<G> {
    companion object {
        const val TAG = "GroupedAdapter"

        @JvmField
        val DEBUG = BuildConfig.DEBUG

        @JvmField
        var TYPE_GROUP_HEADER = R.integer.type_header

        @JvmField
        val TYPE_GROUP_FOOTER = R.integer.type_footer

        @JvmField
        val TYPE_CHILD_GROUP_HEADER = R.integer.type_child_group_header

        @JvmField
        val TYPE_CHILD_GROUP_FOOTER = R.integer.type_child_group_footer

        @JvmField
        val TYPE_CHILD_CHILD = R.integer.type_child_child
    }

    //    private var mOnHeaderClickListener: OnHeaderClickListener? = null
//    private var mOnFooterClickListener: OnFooterClickListener? = null
//    private var mOnChildClickListener: OnChildClickListener? = null

    private var mClickCcListener: ClickListener? = null
    private var mClickHeaderListener: ClickListener? = null
    private var mClickFooterListener: ClickListener? = null
    private var mClickCgHeaderFooterListener: ClickListener? = null

    //保存分组列表的组结构
    protected var mStructures = ArrayList<Group3Structure>()

    //数据是否发生变化。如果数据发生变化，要及时更新组结构。
    public var isDataChanged: Boolean = false
    private var mTempPosition: Int = 0

    private var inflater: LayoutInflater? = null

    private var callback: WeakReferenceOnListChangedCallback<G>? = null
    private var groupList: List<G>? = null
    private var recyclerView: RecyclerView? = null

    // 当孩子为空的时候,如果存在header,就自动移除header
    var childEmptyIsRemoveHeader: Boolean = false
    var ccEmptyRemoveCg: Boolean = false

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

    override fun getItemCount(): Int {
        if (isDataChanged) {
            structureChanged()
        }
        return count()
    }

    override fun getItemViewType(position: Int): Int {
        mTempPosition = position
        val groupPosition = getGroupIndexByPos(position)
        val type = judgeType(position)
        // Ls.d("getItemViewType()..22.position=${position} type=${getTestTypeStr(type)}")
        if (type == TYPE_GROUP_HEADER) {
            return getHeaderViewType(groupPosition)
        } else if (type == TYPE_GROUP_FOOTER) {
            return getFooterViewType(groupPosition)
        } else if (type == TYPE_CHILD_GROUP_HEADER) {
            val cgPosition = getCgIndexInGroup(position, groupPosition)
            return getCgHeaderType(groupPosition, cgPosition)
        } else if (type == TYPE_CHILD_CHILD) {
            val ccIndexInGroup = getCcIndexInGroup(position, groupPosition, 1)
            val cg = getCg(position, groupPosition, 1)
            val cgList = getCgList(groupPosition)
//            Ls.d(
//                "getItemViewType()  itemCount=$itemCount  position=$position " +
//                        "groupPosition=$groupPosition ccIndexInGroup=$ccIndexInGroup  cgListSize=${cgList?.size}"
//            )
            if (cg == null || ccIndexInGroup == -1) {
                val errorText = "Cc为空  itemCount=$itemCount  position=$position " +
                        "groupPosition=$groupPosition ccIndexInGroup=$ccIndexInGroup"
                Ls.d("errorText=${errorText}")
                throw NullPointerException(errorText)
            }
            return getCcType(cgList, getCcList(cg)[ccIndexInGroup], 1)
        } else if (type == TYPE_CHILD_GROUP_FOOTER) {
            return type
        }
        return super.getItemViewType(position)
    }

    private fun getTestTypeStr(type: Int): String {
        if (type == TYPE_GROUP_HEADER) {
            return "头"
        } else if (type == TYPE_GROUP_FOOTER) {
            return "尾"
        } else if (type == TYPE_CHILD_GROUP_HEADER) {
            return "cgHeader"
        } else if (type == TYPE_CHILD_CHILD) {
            return "cc"
        } else if (type == TYPE_CHILD_GROUP_FOOTER) {
            return "cgFooter"
        }
        return "异常88"
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
        val groupPosition = getGroupIndexByPos(position)
        val binding = DataBindingUtil.getBinding<ViewDataBinding>(holder.itemView)!!
        val group = groupList!![groupPosition]
        when (type) {
            TYPE_GROUP_HEADER -> {
                binding.setVariable(BR.gHeader, group)
                if (mClickHeaderListener != null) {
                    binding.setVariable(BR.gHeaderClick, mClickHeaderListener)
                }
                onBindHeaderViewHolder(binding, group, groupPosition)
            }
            TYPE_GROUP_FOOTER -> {
                binding.setVariable(BR.gFooter, group)
                if (mClickFooterListener != null) {
                    binding.setVariable(BR.gFooterClick, mClickFooterListener!!)
                }
                onBindFooterViewHolder(binding, group, groupPosition)
            }
            TYPE_CHILD_GROUP_HEADER -> {
                // Ls.d("onBindViewHolder()..CgHeader.111  position=$position  groupPosition=$groupPosition  cgPosition=$cgPosition ")
                val cg = getCg(position, groupPosition, 2)
                binding.setVariable(BR.cgHeader, cg)
                if (mClickCgHeaderFooterListener != null) {
                    binding.setVariable(BR.cgHeaderClick, mClickCgHeaderFooterListener!!)
                }
                onBindCgHeader(binding, group, cg!!, groupPosition, 11)
            }
            TYPE_CHILD_GROUP_FOOTER -> {
                val cg = getCg(position, groupPosition, 3)
                binding.setVariable(BR.cgFooter, cg)
                if (mClickCgHeaderFooterListener != null) {
                    binding.setVariable(BR.cgFooterClick, mClickCgHeaderFooterListener)
                }
                onBindCgFooterViewHolder(binding, group, cg, groupPosition)
            }
            TYPE_CHILD_CHILD -> {
//                val ccIndex = getCcIndexInGroup(position, groupPosition, 2)
//                val Cc = getCcByChild(getCgList(group), groupPosition, ccIndex)

                val cc = getCc(position, groupPosition)
                //Ls.d("onBindViewHolder()..11..Cc.groupPosition=$groupPosition position=$position childIndex=$childIndex  Cc=$Cc")
                if (cc != null) {
                    binding.setVariable(BR.cc, cc)
                    if (mClickCcListener != null) {
                        binding.setVariable(BR.ccClick, mClickCcListener)
                    }
                    onBindCcViewHolder(binding, group, cc, groupPosition, 12)
                }
            }

        }
        binding.executePendingBindings()
    }

    open fun onBindCgFooterViewHolder(binding: ViewDataBinding, group: G, cg: CG?, groupPosition: Int) {

    }

    private fun getCc(position: Int, groupPosition: Int): CC? {
        val groupIndexInGroup = getGroupIndexInGroup(position, groupPosition)
        var preSize = getGroupHeaderCount(groupPosition)
        getCgList(groupPosition).forEachIndexed { index, cg ->
            preSize += getCgHeaderCount(groupPosition)
            val ccList = getCcList(cg)
            val cgSize = ccList.size
            if (preSize + cgSize > groupIndexInGroup) {
                return ccList[groupIndexInGroup - preSize]
            }
            preSize += cgSize + getCgFooterCount(groupPosition)
        }
        return null
    }

    private fun getCcByChild(cgList: List<CG>, groupPosition: Int, childIndex: Int): CC? {
        var startIndex = 0
        cgList.forEachIndexed { cgIndex, cg ->
            startIndex += getGroupHeaderCount(groupPosition)
            startIndex += getCgHeaderCount(groupPosition)
            val CcList = getCcList(cg)
            CcList.forEachIndexed { CcIndex, Cc ->
                if (childIndex == startIndex) {
                    return Cc
                }
                startIndex++
            }
            startIndex += getCgFooterCount(groupPosition)
        }
        return null
    }

    private fun getCgFooterCount(groupPosition: Int): Int {
        return if (hasCgFooter(groupPosition)) {
            1
        } else {
            0
        }
    }

    private fun getGroupFooterCount(groupPosition: Int): Int {
        return if (hasFooter(groupPosition)) {
            1
        } else {
            0
        }
    }

    private fun getGroupHeaderCount(groupPosition: Int): Int {
        return if (hasHeader(groupPosition)) {
            1
        } else {
            0
        }
    }

    private fun getCgHeaderCount(groupPosition: Int): Int {
        return if (hasCgHeader(groupPosition)) {
            1
        } else {
            0
        }
    }

    open fun setGroupList(groups: List<G>) {
        //if (System.identityHashCode(groupList) == System.identityHashCode(this.groupList)) {
        if (groups === this.groupList) {
            return
        }
        if (recyclerView != null) {
            if (this.groupList is ObservableList<G>) {
                (this.groupList as ObservableList<G>).removeOnListChangedCallback(callback)
                callback = null
            }
            if (groups is ObservableList<G>) {
                callback = WeakReferenceOnListChangedCallback(recyclerView!!, this, groups)
                groups.addOnListChangedCallback(callback)
            }
        }
        this.groupList = groups
        registerCgChangedCallback(groups)
        notifyDataSetChanged()
    }

    /**
     * 给每一组里的 cgList的添加数据改变监听
     */
//    fun registerCgChangedCallback(groupList: List<G>) {
    fun registerCgChangedCallback(groupList: Any) {
        // 为每组的childList添加监听
        val list = groupList as? List<G>
        list ?: return
        for (group in list) {
            val cgList = getCgList(group)
            registerCgListChangedCallback(cgList)
        }
    }

    fun registerCcListChangedCallback2(cgObj: Any) {
        val cg = cgObj as? CG
        if (cg != null) {
            val CcList = getCcList(cg)
            registerCcCallback(CcList)
        }
    }

    open fun registerCgListChangedCallback(cgList: List<CG>) {
        if (cgList is ObservableList) {
            val childListCallback = Group3CgAndCcListChangCallback(this, true)
            cgList.addOnListChangedCallback(childListCallback as ObservableList.OnListChangedCallback<Nothing>)
            cgList.forEach { cg ->
                val CcList = getCcList(cg)
                registerCcCallback(CcList)
            }
        }
    }

    private fun registerCcCallback(CcList: List<CC>) {
        if (CcList is ObservableList) {
            CcList.addOnListChangedCallback(
                Group3CgAndCcListChangCallback(
                    this,
                    false
                ) as ObservableList.OnListChangedCallback<Nothing>
            )
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
        if (judgeType(position) == TYPE_GROUP_HEADER || judgeType(position) == TYPE_GROUP_FOOTER) {
            val p = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            p.isFullSpan = true
        }
    }


    open fun getHeaderViewType(groupPosition: Int): Int {
        return TYPE_GROUP_HEADER
    }

    open fun getFooterViewType(groupPosition: Int): Int {
        return TYPE_GROUP_FOOTER
    }

    open fun getCgHeaderType(groupPosition: Int, childPosition: Int): Int {
        return TYPE_CHILD_GROUP_HEADER
    }

    open fun isSupportMultiTypeCc(): Boolean {
        return false
    }

    open fun getCcType(cgList: List<CG>, cc: CC, childInCgIndex: Int): Int {
        return TYPE_CHILD_CHILD
    }

    private fun getLayoutId(position: Int, viewType: Int): Int {
        val type = judgeType(position)
        if (type == TYPE_GROUP_HEADER) {
            return getHeaderLayout(viewType)
        } else if (type == TYPE_GROUP_FOOTER) {
            return getFooterLayout(viewType)
        } else if (type == TYPE_CHILD_GROUP_HEADER) {
            return getCgHeaderLayout(viewType)
        } else if (type == TYPE_CHILD_GROUP_FOOTER) {
            return getCgFooterLayout(viewType)
        } else if (type == TYPE_CHILD_CHILD) {
            return getCcLayout(viewType)
        }
        return 0
    }

    private fun count(): Int {
        return countGroupRangeCount(0, mStructures.size)
    }

    /**
     * 判断item的type 头部 尾部 和 子项
     *
     * @param position
     * @return
     */
    fun judgeType(position: Int): Int {  // 6
        val groupPosition = getGroupIndexByPos(position)
        val groupIndexInGroup = getGroupIndexInGroup(position, groupPosition)
        val cgList = getCgList(getGroup(groupPosition))
        val headerCount = getGroupHeaderCount(groupPosition)
        var index = 0
        val cgHeaderCount = getCgHeaderCount(groupPosition)
        val cgFooterCount = getCgFooterCount(groupPosition)
//        Ls.d(
//            "judgeType()...111..position=$position  groupPosition=$groupPosition  " +
//                    "groupIndexInGroup=$groupIndexInGroup  hCount=$headerCount cgHCount=$cgHeaderCount"
//        )
        // 判断头
        if (headerCount > 0 && index == groupIndexInGroup) {
            return TYPE_GROUP_HEADER
        }
        index += headerCount
        cgList.forEachIndexed { cgIndexInGroup, cg ->
            // 判断Cg的头
            if (cgHeaderCount > 0 && index == groupIndexInGroup) {
                return TYPE_CHILD_GROUP_HEADER
            }
            index += cgHeaderCount
            // 判断cc
            getCcList(cg).forEachIndexed { ccIndex, cc ->
                if (index == groupIndexInGroup) {
                    return TYPE_CHILD_CHILD
                }
                index++
            }
            // 判断Cg的尾
            if (cgFooterCount > 0 && index == groupIndexInGroup) {
                return TYPE_CHILD_GROUP_FOOTER
            }
            index += cgFooterCount
        }
        // 判断尾
        val footCount = getGroupFooterCount(groupPosition)
        if (footCount > 0 && index == groupIndexInGroup) {
            return TYPE_GROUP_FOOTER
        }
        index += footCount
        throw IndexOutOfBoundsException(
            "can't determine the item type of the position." +
                    "position = " + position + ",item count = " + getItemCount() + " index=$index " +
                    " footCount=$footCount  groupIndexInGroup=$groupIndexInGroup"
        )
    }

    /**
     * 判断item的type 头部 尾部 和 子项
     *
     * @param position
     * @return
     */
    fun judgeType3(position: Int): Int {  // 6
        var indexInGroup = 0
        val groupCount = mStructures.size
        for (groupPosition in 0 until groupCount) {
            val structure = mStructures[groupPosition]
            if (structure.hasHeader()) {
                indexInGroup += 1
                if (position < indexInGroup) {
                    // Ls.d("judgeType()..1111...header=$position")
                    return TYPE_GROUP_HEADER
                }
            }
            indexInGroup += structure.childNumInGroup // 8 = 7 + 1(header)
            // 如果position == 2 ,实际是 child1-1 , groupPosition = 0
            // Ls.d("judgeType()..1111...header  position=$position  indexInGroup=$itemIndex")
            if (position < indexInGroup) {
                val groupStartPos = indexInGroup - structure.childNumInGroup - structure.headerCount
                val cgList = getCgList(groupList!![groupPosition]!!)
//                Ls.d(
//                    "judgeType()..3333..Cc  position=$position  groupStartPos=$groupStartPos  indexInGroup=$itemIndex  isCc=${isCc(
//                        groupStartPos,
//                        groupPosition,
//                        position,
//                        cgList
//                    )}"
//                )
                if (isCc(groupStartPos, groupPosition, position, cgList)) {
                    return TYPE_CHILD_CHILD
                }
                if (structure.hasCgFooter()) {
                    if (isCgFooter(groupStartPos, position, groupPosition, cgList)) {
//                        Ls.d(
//                            "judgeType()..444...isCgFooter..position=$position  indexInGroup=$itemIndex  isFooter=${isCgFooter(
//                                groupStartPos,
//                                position,
//                                cgList
//                            )}"
//                        )
                        return TYPE_CHILD_GROUP_FOOTER
                    }
                }
//                Ls.d(
//                    "judgeType()..5555...isCgFooter..position=$position  indexInGroup=$itemCount  isFooter=${isCgFooter(
//                        groupStartPos,
//                        position,
//                        cgList
//                    )}"
//                )
                if (structure.hasCgHeader()) {
                    if (isCgHeader(groupStartPos, position, groupPosition, cgList)) {
                        return TYPE_CHILD_GROUP_HEADER
                    }
                }
            }

            // Ls.d("judgeType()..444...position=$position  itemCount=$itemCount")
//            Ls.d("judgeType()...1111.pos=$position  itemCount=$itemCount  groupPosition=$groupPosition  " +
//                    "childNumInGroup=${structure.childNumInGroup}  hasFooter=${structure.hasFooter()}")

            if (structure.hasFooter()) {
                indexInGroup += 1
                if (position < itemCount) {
                    return TYPE_GROUP_FOOTER
                }
            }
        }
        throw IndexOutOfBoundsException(
            "can't determine the item type of the position." +
                    "position = " + position + ",item count = " + getItemCount()
        )
    }

    /**
     * @param groupStartPos 如下
     * 组头1
     * c1      childInex=0
     *  c1-1   childInex=1
     *  c1-2   childInex=2
     * 组尾1
     * 组头2
     * c1      childInex=0
     *  c1-1   childInex=1
     *  c1-2   childInex=2
     * 组尾2
     *  0->4之间的pos, groupStartPos = 0
     *  5->9之间的pos, groupStartPos = 5
     */
    private fun isCc(groupStartPos: Int, groupPosition: Int, position: Int, cgList: List<CG>): Boolean {
        var index = 0
        cgList.forEach { child ->
            if (hasCgHeader(groupPosition)) {
                index++
                if (index + groupStartPos == position) {
                    return false
                }
            }
            index += getCcList(child).size

            if (hasCgFooter(groupPosition)) {
                index++
                if (groupStartPos + index == position) {
                    return false
                }
            }
        }
        return true
    }

    private fun isCgHeader(groupStartPos: Int, position: Int, groupPosition: Int, cgList: List<CG>): Boolean {
        var index = 0
        cgList.forEachIndexed { childIndex, child ->
            index += getCgHeaderCount(groupPosition)
            if (position == index + groupStartPos) {
                return true
            }
            index += getCcList(child).size + getCgFooterCount(groupPosition)
        }
        return false
    }

    private fun isCgFooter(groupStartPos: Int, position: Int, groupPosition: Int, cgList: List<CG>): Boolean {
        var index = 0
        cgList.forEach { child ->
            index += getCcList(child).size + getCgHeaderCount(groupPosition) + getCgFooterCount(groupPosition)  // 加1是footer的数量
            if (position == index + groupStartPos) {
                return true
            }
        }
        return false
    }

    /**
     * 重置组结构列表
     */
    private fun structureChanged() {
        mStructures.clear()
        val groupCount = getGroupCount()
        for (i in 0 until groupCount) {
            val groupStructure = Group3Structure(hasHeader(i), hasFooter(i), getCountInGroup(i))
            groupStructure.setHasCgFooter(hasCgFooter(i))
            groupStructure.setHasCgHeader(hasCgHeader(i))
            mStructures.add(groupStructure)
        }
        isDataChanged = false
//        if (DEBUG) {
//            Ls.d("重置组结构列表...666666...size=${mStructures.size}")
//        }
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

    fun getGroupPositionByCgList(cgList: List<*>): Int {
        val cgs = cgList as? List<CG>
        var mGroupIndex = -1
        if (cgs != null) {
            run breaking@{
                groupList!!.forEachIndexed { groupPosition, group ->
                    if (getCgList(group) === cgList) {
                        mGroupIndex = groupPosition
                        return@breaking
                    }
                }
            }
        }
        return mGroupIndex
    }

    fun getGroup(groupPosition: Int): G {
        return groupList!![groupPosition]!!
    }

    /**
     * 根据列表,获取属于哪个Cg 和 Group
     */
    fun getGroupPositionByCcList(CcList: List<*>): Pair<Int, Int> {
        val Ccs = CcList as? List<CC>
        var mGroupIndex = -1
        Ccs ?: return Pair(mGroupIndex, mGroupIndex)
        var mCgIndex = -1
        run breaking@{
            groupList!!.forEachIndexed { groupPosition, group ->
                getCgList(group).forEachIndexed { cgIndex, cg ->
                    if (getCcList(cg) === Ccs) {
                        mGroupIndex = groupPosition
                        mCgIndex = cgIndex
                        return@breaking
                    }
                }
            }
        }
        return Pair<Int, Int>(mGroupIndex, mCgIndex)
    }

    private fun getGroupIndexInGroup(position: Int, groupPosition: Int): Int {
        val itemCount = countGroupRangeCount(0, groupPosition + 1)
        val s = mStructures[groupPosition]
        val index = s.childNumInGroup - (itemCount - position)
//        Ls.d(
//            "getGroupIndexInGroup().. position=$position childNumInGroup=${s.childNumInGroup}  " +
//                    "itemCount=$itemCount groupPosition=$groupPosition  index=$index"
//        )
        return index
    }

    /**
     * 根据下标计算position所在的组（groupPosition）
     *
     * @param position 下标
     * @return 组下标 groupPosition
     */
    private fun getGroupIndexByPos(position: Int): Int {
        var count = 0
        val groupCount = mStructures.size
        for (i in 0 until groupCount) {
            count += countGroupCount(i)
            if (position < count) {
                return i
            }
        }
        return -1
    }

    private fun getCg(position: Int, groupPosition: Int, flag: Int): CG {
        val groupIndexInGroup = getGroupIndexInGroup(position, groupPosition)
        // val childPosition = getCcIndexInGroup(position, groupPosition,3)
        val cgList = getCgList(groupList!![groupPosition]!!)
        var upIndex = getGroupHeaderCount(groupPosition)
        val cgHeaderCount = getCgHeaderCount(groupPosition)
        val cgFooterCount = getCgFooterCount(groupPosition)
        cgList.forEachIndexed { index, cg ->
            upIndex += cgHeaderCount + getCcList(cg).size + cgFooterCount
            if (upIndex > groupIndexInGroup) {
                return cg
            }
        }
        // Ls.d("getCg()....groupPosition=$groupPosition position=$position childPosition=$childPosition")
        throw NullPointerException("Cg为空...groupPosition=$groupPosition groupIndexInGroup=$groupIndexInGroup  flag=$flag")
    }

    /**
     * 返回1个组里,每个Child在组里的索引
     * c1
     *  c1-1
     *  c1-2
     * c2
     *  c2-1
     *  c2-2
     * 比如: position=0、2, 返回 0 和 0
     */
    private fun getCgIndexInGroup(position: Int, groupPosition: Int): Int {
        val childPosition = getCcIndexInGroup(position, groupPosition, 4)
        var upIndex = 0
        run breaking@{
            getCgList(groupList!![groupPosition]!!).forEachIndexed { index, cg ->
                upIndex += getCcList(cg).size + getCgHeaderCount(groupPosition)
                if (upIndex > childPosition) {
                    return index
                }
            }
        }
        return 0
    }


    /**
     * 根据下标计算position在组中位置（childPosition）,
     * 因为每个child里,有不同的Cc数量,所以得计算出归属于哪个child分组的,比如:
     * 组头
     * c1    childInex=0
     *  c1-1      1
     *  c1-2    2
     * c2   3
     *  c2-1  4
     *  c2-2  5
     *  组尾
     * 需要这样的结果: 如果pos=0,1,2, childPosition=0, pos=3,4,5 childPosition=1
     * @param groupPosition 所在的组
     * @param position      下标
     * @return 子项下标 childPosition
     *
     */
    private fun getCcIndexInGroup(position: Int, groupPosition: Int, flag: Int): Int {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val itemCount = countGroupRangeCount(0, groupPosition + 1)
            val structure = mStructures[groupPosition]
            val groupIndexInGroup = getGroupIndexInGroup(position, groupPosition)
//            Ls.d("getCcIndexInGroup()..position=$position  groupPosition=$groupPosition groupIndexInGroup=$groupIndexInGroup")
            var ccIndex = -1
            if (true) {
                var preSize = getGroupHeaderCount(groupPosition)
                getCgList(groupPosition).forEachIndexed { index, cg ->
                    preSize += getCgHeaderCount(groupPosition)
                    val ccList = getCcList(cg)
                    val cgSize = ccList.size
                    if (preSize + cgSize > groupIndexInGroup) {
                        return groupIndexInGroup - preSize
                    }
                    preSize += cgSize + getCgFooterCount(groupPosition)
//                    getCcList(cg).forEachIndexed { cI, cc ->
//                        if (preSize == groupIndexInGroup) {
//                            return cI
//                        }
//                        preSize++
//                    }
                }
                return -1
                // 7 - 1 + 1
//                ccIndex = structure.childNumInGroup - (itemCount - position) + structure.headerCount
//                ccIndex = groupIndexInGroup - getGroupHeaderCount(groupPosition) - getCgHeaderCount(groupPosition)
//                Ls.d(
//                    "getCcIndexInGroup()...111..position=$position  " +
//                            "groupPosition=$groupPosition  ccIndex=$ccIndex  " +
//                            "groupIndexInGroup=${groupIndexInGroup} itemCount=$itemCount  childNumInGroup=${structure.childNumInGroup} " +
//                            "headerCount=${structure.headerCount}  flag=$flag"
//                )
//                return ccIndex
            }
//            getCgList(groupPosition).forEach { cg ->
//                
//            }

            // val ccIndex = structure.childNumInGroup - (itemCount - position) + structure.headerCount
            ccIndex = itemCount - position
//            if (!structure.hasFooter() && hasCgHeader(groupPosition)) {
//                // todo 这里添加了个判断
//                childIndex--
//            }

            if (!structure.hasFooter()) {
                // todo 这里添加了个判断 hasCgHeader
                if (structure.headerCount > 0 || hasCgHeader(groupPosition)) {
                }
            }
            return ccIndex
        }
        return -1
    }

    private fun getRealChildPositionByChildPos(groupPosition: Int, childPos: Int): Int {
        var indexSize = 0
        getCgList(groupList!![groupPosition]).forEachIndexed { childIndex, child1 ->
            indexSize++
            // 第1个child1组里
            if (childPos == indexSize - 1) {
                return childIndex
            }

            indexSize += getCcList(child1).size
        }
//        run breaking@{
//            group.childList.forEachIndexed { index, child ->
//                if (childPosition > upIndex) {
//                    childIndex = childPosition - upIndex
//                } else {
//                    return@breaking
//                }
//                upIndex += child.CcList.size
//            }
//        }
        return 0
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
                return countGroupRangeCount(0, groupPosition)
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
            } else countGroupRangeCount(0, groupPosition + 1) - 1
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
            if (structure.childNumInGroup > childPosition) {
                val itemCount = countGroupRangeCount(0, groupPosition)
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
    fun countGroupCount(groupPosition: Int): Int {
        var itemCount = 0
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val structure = mStructures[groupPosition]
            itemCount += structure.childNumInGroup
        }
        return itemCount
    }

    /**
     * 计算多个组的项的总和
     *
     * @return
     */
    fun countGroupRangeCount(start: Int, count: Int): Int {
        var itemCount = 0
        val size = mStructures.size
        var i = start
        while (i < size && i < start + count) {
            itemCount += countGroupCount(i)
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
        val itemCount = countGroupCount(groupPosition)
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
            itemCount = countGroupRangeCount(groupPosition, groupPosition + count)
        } else {
            itemCount = countGroupRangeCount(groupPosition, mStructures.size)
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
                if (structure.childNumInGroup >= childPosition + count) {
                    notifyItemRangeChanged(index, count)
                } else {
                    notifyItemRangeChanged(index, structure.childNumInGroup - childPosition)
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
                notifyItemRangeChanged(index, structure.childNumInGroup)
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
        val itemCount = countGroupCount(groupPosition)
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
            itemCount = countGroupRangeCount(groupPosition, groupPosition + count)
        } else {
            itemCount = countGroupRangeCount(groupPosition, mStructures.size)
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
            structure.childNumInGroup = structure.childNumInGroup - 1
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
                val childCount = structure.childNumInGroup
                var removeCount = count
                if (childCount < childPosition + count) {
                    removeCount = childCount - childPosition
                }
                // Ls.d("notifyChildRangeRemoved()..index=$index  itemCount=$itemCount  childCount=$childCount  removeCount=$removeCount  ")
                notifyItemRangeRemoved(index, removeCount)
                notifyItemRangeChanged(index, itemCount - removeCount)
                structure.childNumInGroup = childCount - removeCount
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
                val itemCount = structure.childNumInGroup
                notifyItemRangeRemoved(index, itemCount)
                notifyItemRangeChanged(index, getItemCount() - itemCount)
                structure.childNumInGroup = 0
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
        val structure = Group3Structure(
            hasHeader(groupPos),
            hasFooter(groupPos), getCountInGroup(groupPos)
        )
        if (groupPos >= 0 && groupPos < mStructures.size) {
            mStructures.add(groupPos, structure)
        } else {
            mStructures.add(structure)
            groupPos = mStructures.size - 1
        }

        val index = countGroupRangeCount(0, groupPos)
        val itemCount = countGroupCount(groupPos)
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
        val list = ArrayList<Group3Structure>()
        for (i in 0 until count) {
            val structure = Group3Structure(
                hasHeader(i),
                hasFooter(i), getCountInGroup(i)
            )
            list.add(structure)
        }

        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            mStructures.addAll(groupPosition, list)
        } else {
            mStructures.addAll(list)
            groupPosition = mStructures.size - list.size
        }

        val index = countGroupRangeCount(0, groupPosition)
        val itemCount = countGroupRangeCount(groupPosition, count)
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
            val index = countGroupRangeCount(0, groupPosition)
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
        if (groupPosition >= 0 && groupPosition < mStructures.size && 0 > getPositionForGroupFooter(groupPosition)) {
            val structure = mStructures[groupPosition]
            structure.setHasFooter(true)
            val index = countGroupRangeCount(0, groupPosition + 1)
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
                index = countGroupRangeCount(0, groupPosition)
                index += structure.childNumInGroup
            }
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
            var index = countGroupRangeCount(0, groupPosition)
            val structure = mStructures[groupPosition]
            if (structure.hasHeader()) {
                index++
            }
            if (childPosition < structure.childNumInGroup) {
                index += childPosition
            } else {
                index += structure.childNumInGroup
            }
            if (count > 0) {
                structure.childNumInGroup = structure.childNumInGroup + count
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
            var index = countGroupRangeCount(0, groupPosition)
            val structure = mStructures[groupPosition]
            if (structure.hasHeader()) {
                index++
            }
            val itemCount = getCountInGroup(groupPosition)
            if (itemCount > 0) {
                structure.childNumInGroup = itemCount
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
    fun setClickCcListener(listener: ClickListener) {
        mClickCcListener = listener
    }

    /**
     * 设置子项点击事件
     *
     * @param listener
     */
//    fun setOnChildClickListener(listener: OnChildClickListener) {
//        mOnChildClickListener = listener
//    }
    fun setClickChildListener(listener: ClickListener) {
        mClickCcListener = listener
    }

    fun setClickHeaderListener(listener: ClickListener) {
        mClickHeaderListener = listener
    }

    fun setClickCgHeaderFooterListener(listener: ClickListener) {
        mClickCgHeaderFooterListener = listener
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

    fun removeGroupPosition(groupPosition: Int) {
        if (groupPosition >= 0 && groupPosition < groupList!!.size) {
            if (groupList!! is ArrayList) {
                (groupList!! as ArrayList).removeAt(groupPosition)
            }
        }
    }

    fun removeCgPosition(groupPosition: Int, cgPosition: Int) {
        if (groupPosition >= 0 && groupPosition < groupList!!.size) {
            val g = groupList!![groupPosition]
            val cgList = getCgList(g)
            if (cgPosition >= 0 && cgPosition < cgList.size)
                if (cgList is ArrayList) {
                    (cgList as ArrayList).removeAt(cgPosition)
                }
        }
    }

    fun setCgEmptyRemoveGroup(isRemoveHeaderWhenChildEmpty: Boolean = true): Group3RecyclerViewAdapter<G, CG, CC> {
        childEmptyIsRemoveHeader = isRemoveHeaderWhenChildEmpty
        return this
    }

    fun setccEmptyRemoveCg(remove: Boolean = true): Group3RecyclerViewAdapter<G, CG, CC> {
        ccEmptyRemoveCg = remove
        return this
    }

    private fun getCountInGroup(groupPosition: Int): Int {
        return getCountInGroup(groupPosition, groupList!![groupPosition]!!)
    }

    /**
     *  * 组头
     * c1      childInex=0
     *  c1-1   childInex=1
     *  c1-2   childInex=2
     * c2      childInex=3
     *  c2-1   childInex=4
     *  c2-2   childInex=5
     *  组尾
     */
    private fun getCountInGroup(groupPosition: Int, group: G): Int {
        var totalSize = 0
        totalSize += getGroupHeaderCount(groupPosition) // Header
        getCgList(group).forEach { cg ->
            if (hasCgHeader(groupPosition)) {
                totalSize++  // cgHeader 也是1个
            }
            totalSize += getCcList(cg).size
            // 如果每个 cg 都有Footer,那么加1
            if (hasCgFooter(groupPosition)) {
                totalSize++
            }
        }
        totalSize += getGroupFooterCount(groupPosition) // Footer
        return totalSize
    }

    open fun hasCgFooter(groupPosition: Int): Boolean {
        return false
    }

    open fun hasCgHeader(groupPosition: Int): Boolean {
        return true
    }

    open fun getCgFooterLayout(viewType: Int): Int {
        return 0
    }

    fun getCgList(groupPosition: Int): List<CG> {
        return getCgList(getGroup(groupPosition))
    }


    open fun getFooterLayout(viewType: Int): Int {
        return 1
    }

    open fun hasFooter(groupPosition: Int): Boolean {
        return false
    }

    abstract fun getHeaderLayout(viewType: Int): Int

    abstract fun getCgHeaderLayout(viewType: Int): Int

    abstract fun getCcLayout(viewType: Int): Int

    abstract fun hasHeader(groupPosition: Int): Boolean


    abstract fun getCcList(cg: CG): List<CC>

    abstract fun getCgList(group: G): List<CG>

    open fun onBindHeaderViewHolder(vBinding: ViewDataBinding, group: G, groupPosition: Int) {
    }

    open fun onBindFooterViewHolder(vBinding: ViewDataBinding, group: G, groupPosition: Int) {

    }

    open fun onBindCgHeader(
        vBinding: ViewDataBinding,
        group: G, cg: CG,
        groupPosition: Int,
        childPosition: Int
    ) {

    }

    open fun onBindCcViewHolder(
        vBinding: ViewDataBinding,
        group: G, Cc: CC,
        groupPosition: Int,
        childPosition: Int
    ) {
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
        private var groupAdapter: Group3RecyclerViewAdapter<G, *, *>,
        groupList: ObservableList<G>
    ) : ObservableList.OnListChangedCallback<ObservableList<G>>() {
        // internal val adapterRef: WeakReference<Group3RecyclerViewAdapter<G, *>> = AdapterReferenceCollector.createRef
        // (groupAdapter, groupList, this)

        override fun onChanged(sender: ObservableList<G>) {
            if (DEBUG) {
                Ls.d("Group3RecyclerViewAdapter()....onChanged()...1111..")
            }
            groupAdapter.notifyDataChanged()
        }

        override fun onItemRangeChanged(
            sender: ObservableList<G>,
            positionStart: Int,
            itemCount: Int
        ) {
            if (DEBUG) {
                Ls.d("Group3RecyclerViewAdapter()....onItemRangeChanged()...2222..positionStart=$positionStart  itemCount=$itemCount")
            }
            groupAdapter.notifyDataChanged()
        }

        override fun onItemRangeInserted(
            sender: ObservableList<G>,
            positionStart: Int,
            itemCount: Int
        ) {
            if (DEBUG) {
                Ls.d("Group3RecyclerViewAdapter()..onItemRangeInserted()...33333...sender=${sender.size}  itemCount=$itemCount")
            }
            groupAdapter.registerCgListChangedCallback(groupAdapter.getCgList(sender[positionStart]) as List<Nothing>)
            groupAdapter.notifyDataChanged()
        }

        override fun onItemRangeMoved(
            sender: ObservableList<G>,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            if (DEBUG) {
                Ls.d("Group3RecyclerViewAdapter()....onItemRangeMoved()..444.. itemCount=$itemCount")
            }
            groupAdapter.notifyDataChanged()
        }

        // clear() 会执行这
        override fun onItemRangeRemoved(
            sender: ObservableList<G>,
            positionStart: Int,
            itemCount: Int
        ) {
            if (DEBUG) {
                Ls.d("Group3RecyclerViewAdapter()....onItemRangeRemoved()...555...positionStart=$positionStart itemCount=$itemCount")
            }

            groupAdapter.notifyDataChanged()
        }
    }


    //    interface OnHeaderClickListener {
//        fun onHeaderClick(
//            adapter: Group3RecyclerViewAdapter<*, *>,
//            vBinding:ViewDataBinding,
//            groupPosition: Int
//        )
//    }
//
//    interface OnFooterClickListener {
//        fun onFooterClick(
//            adapter: Group3RecyclerViewAdapter<*, *>,
//            vBinding:ViewDataBinding,
//            groupPosition: Int
//        )
//    }
//
//    interface OnChildClickListener {
//        fun onChildClick(
//            adapter: Group3RecyclerViewAdapter<*, *>, vBinding:ViewDataBinding,
//            groupPosition: Int, childPosition: Int
//        )
//    }
    private class BindingViewHolder internal constructor(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}


