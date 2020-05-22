package me.lx.sample.model

import android.widget.Toast
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModel
import me.lx.rv.RvThreeLevelGroupBindListener
import me.lx.rv.click.ClickListener
import me.lx.rv.group.BaseFun1ClickGroupListener
import me.lx.rv.group.BaseFun2ClickGroupListener
import me.lx.rv.group.ClickGroupListener
import me.lx.rv.group.ThreeLevelGroupedRecyclerViewAdapter
import me.lx.sample.MyApp
import me.lx.sample.group.adapter.ThreeLevelGroupAdapter
import me.lx.sample.group.entity.TwoLevelGroupEntity
import me.lx.sample.group.entity.TwoLevelGroupEntity.CgEntity
import me.lx.sample.group.entity.TwoLevelGroupEntity.CgEntity.CcEntity
import me.lx.sample.group.model.GroupModel
import java.util.AbstractList
import java.util.function.BiConsumer

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2020/1/17 14:06
 *  version: 1.0
 *  desc: 3级分组,child -> 包含child
 */
class ThreeLevelGroupModel : ViewModel(),
    RvThreeLevelGroupBindListener<TwoLevelGroupEntity, TwoLevelGroupEntity.CgEntity, TwoLevelGroupEntity.CgEntity.CcEntity> {

    companion object {
        const val click_g_header = 1
        const val click_g_footer = 2
        const val click_cc = 3
        const val click_cg_header = 4
        const val click_cg_footer = 5
    }

    // 数据 -> item
    // 数据
    val groupList = GroupModel.getTwoGroupGroupOb(8, 3) // 普通列表数据

    val gHeaderFooterClick = object : BaseFun2ClickGroupListener<TwoLevelGroupEntity, Int>() {
        override fun clickGroup(item: TwoLevelGroupEntity, flag: Int) {
            when (flag) {
                click_g_header -> {
                    Toast.makeText(MyApp.sContext, item.headerText, Toast.LENGTH_SHORT).show()
                }
                click_g_footer -> {
                    Toast.makeText(MyApp.sContext, item.footerText, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val cgHeaderFooterClick = object : BaseFun2ClickGroupListener<TwoLevelGroupEntity.CgEntity, Int>() {
        override fun clickGroup(item: TwoLevelGroupEntity.CgEntity, flag: Int) {
            when (flag) {
                click_cg_header -> {
                    Toast.makeText(MyApp.sContext, item.cgHeaderText, Toast.LENGTH_SHORT).show()
                }
                click_cg_footer -> {
                    Toast.makeText(MyApp.sContext, item.cgFooterText, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val ccClick = object : BaseFun2ClickGroupListener<TwoLevelGroupEntity.CgEntity.CcEntity, Int>() {
        override fun clickGroup(item: CcEntity, flag: Int) {
            Toast.makeText(MyApp.sContext, item.childChildText, Toast.LENGTH_SHORT).show()
        }
    }
    val groupAdapter = ThreeLevelGroupAdapter()

    override fun getGroups(): AbstractList<TwoLevelGroupEntity> {
        return groupList
    }

    override fun getAdapter(): ThreeLevelGroupedRecyclerViewAdapter<TwoLevelGroupEntity, CgEntity, CcEntity> {
        return groupAdapter
    }

    override fun getClickCgHeaderFooterListener(): ClickListener? {
        return cgHeaderFooterClick
    }

    override fun getClickHeaderListener(): ClickListener? {
        return gHeaderFooterClick
    }

    override fun getClickFootListener(): ClickListener? {
        return gHeaderFooterClick
    }

    override fun getClickChildChildListener(): ClickGroupListener? {
        return ccClick
    }
}