package me.lx.sample.group.adapter

import androidx.databinding.ViewDataBinding
import me.lx.rv.group.BaseFun1ClickGroupListener
import me.lx.rv.group.ThreeLevelGroupedRecyclerViewAdapter
import me.lx.rv.tools.Ls
import me.lx.sample.BR
import me.lx.sample.R
import me.lx.sample.group.entity.TwoLevelGroupEntity
import me.lx.sample.group.entity.TwoLevelGroupEntity.CgEntity
import me.lx.sample.group.entity.TwoLevelGroupEntity.CgEntity.CcEntity
/**
 * 3级分组
 * group1-header
 *  child1
 *      child1-1
 *      child2-2
 *  child2
 *      child2-1
 *      child2-2
 */
open class ThreeLevelGroupAdapter :
    ThreeLevelGroupedRecyclerViewAdapter<TwoLevelGroupEntity, TwoLevelGroupEntity.CgEntity, CcEntity>() {

    companion object {
        const val cc_type1 = 2
        const val cc_type2 = 3
    }

//    var childGroupFooterClickEvent: BaseFun1ClickGroupListener<TwoLevelGroupEntity.CgEntity>? = null
    override fun getHeaderLayout(viewType: Int): Int {
        return R.layout.adapter_header_two_level
    }

    override fun getFooterLayout(viewType: Int): Int {
        return R.layout.adapter_footer_two_level
    }

    override fun getChildGroupHeaderLayout(viewType: Int): Int {
        return R.layout.adapter_cg_header_two_level
    }

    override fun getChildGroupFooterLayout(viewType: Int): Int {
        return R.layout.adapter_child_group_footer
    }

    override fun getChildChildLayout(viewType: Int): Int {
//        if (viewType == cc_type1) {
//            return R.layout.adapter_child_child_two_level
//        }
        return R.layout.adapter_cc2
    }

    override fun getChildGroupList(group: TwoLevelGroupEntity): List<CgEntity> {
        return group.childGroupList
    }

    override fun getChildChildList(childGroup: CgEntity): List<CcEntity> {
        return childGroup.childChildList
    }

    override fun hasFooter(groupPosition: Int): Boolean {
        //  Ls.d("hasFooter()..111111..groupPosition=$groupPosition")
        // if (groupPosition < 2) return false
        return true
    }

    override fun hasChildGroupFooter(groupPosition: Int): Boolean {
          return true
        // return groupPosition %2==0
    }

    override fun hasHeader(groupPosition: Int): Boolean {
        //return getItems()[groupPosition].childGroupList.size > 0
        //  return groupPosition > 0
        return true
    }

    override fun hasChildGroupHeader(groupPosition: Int): Boolean {
        //  return groupPosition % 2 != 0
        return true
    }

    override fun isSupportMultiTypeChildChild(): Boolean {
        return true
    }


    override fun getChildChildType(groupPosition: List<CgEntity>, childPosition: CcEntity, childInGroupIndex: Int): Int {
        // Ls.d("getChildChildType()...childInGroupIndex=$childInGroupIndex")
        if (childInGroupIndex == 3 || childInGroupIndex == 7) {
            return cc_type1
        }
        return cc_type2
    }

    override fun onBindChildGroupFooterViewHolder(
        binding: ViewDataBinding,
        group: TwoLevelGroupEntity,
        childGroup: CgEntity?,
        groupPosition: Int
    ) {
//        binding.setVariable(BR.childGroupFooterClick, childGroupFooterClickEvent)
//        binding.setVariable(BR.childGroup, childGroup)
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
        child: CgEntity,
        groupPosition: Int,
        childPosition: Int
    ) {
        // 而外设置变量
    }


}
