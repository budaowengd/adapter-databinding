package me.lx.sample.group.adapter

import androidx.databinding.ViewDataBinding
import me.lx.rv.group.BaseFun1ClickGroupListener
import me.lx.rv.group.Group3RecyclerViewAdapter
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
open class Group3TestAdapter :
    Group3RecyclerViewAdapter<TwoLevelGroupEntity, TwoLevelGroupEntity.CgEntity, CcEntity>() {

    companion object {
        const val cc_type1 = 2
        const val cc_type2 = 3
    }

//    var CgFooterClickEvent: BaseFun1ClickGroupListener<TwoLevelGroupEntity.CgEntity>? = null
    override fun getHeaderLayout(viewType: Int): Int {
        return R.layout.adapter_header_two_level
    }

    override fun getFooterLayout(viewType: Int): Int {
        return R.layout.adapter_footer_two_level
    }

    override fun getCgHeaderLayout(viewType: Int): Int {
        return R.layout.adapter_cg_header_two_level
    }

    override fun getCgFooterLayout(viewType: Int): Int {
        return R.layout.adapter_child_group_footer
    }

    override fun getCcLayout(viewType: Int): Int {
//        if (viewType == cc_type1) {
//            return R.layout.adapter_child_child_two_level
//        }
        return R.layout.adapter_cc2
    }

    override fun getCgList(group: TwoLevelGroupEntity): List<CgEntity> {
        return group.childGroupList
    }

    override fun getCcList(cg: CgEntity): List<CcEntity> {
        return cg.childChildList
    }

    override fun hasFooter(groupPosition: Int): Boolean {
        //  Ls.d("hasFooter()..111111..groupPosition=$groupPosition")
        // if (groupPosition < 2) return false
        return true
    }

    override fun hasCgFooter(groupPosition: Int): Boolean {
          return true
        // return groupPosition %2==0
    }

    override fun hasHeader(groupPosition: Int): Boolean {
        //return getItems()[groupPosition].CgList.size > 0
        //  return groupPosition > 0
        return true
    }

    override fun hasCgHeader(groupPosition: Int): Boolean {
        //  return groupPosition % 2 != 0
        return true
    }

    override fun isSupportMultiTypeCc(): Boolean {
        return true
    }


    override fun getCcType(groupPosition: List<CgEntity>, childPosition: CcEntity, childInGroupIndex: Int): Int {
        // Ls.d("getCcType()...childInGroupIndex=$childInGroupIndex")
        if (childInGroupIndex == 3 || childInGroupIndex == 7) {
            return cc_type1
        }
        return cc_type2
    }
}
