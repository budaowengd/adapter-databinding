package me.lx.sample.model

import android.widget.Toast
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModel
import me.lx.rv.RvBindGroup3Listener
import me.lx.rv.click.ClickListener
import me.lx.rv.group.BaseFun1ClickGroupListener
import me.lx.rv.group.BaseFun2ClickGroupListener
import me.lx.rv.group.ClickGroupListener
import me.lx.rv.group.Group3RecyclerViewAdapter
import me.lx.sample.MyApp
import me.lx.sample.group.adapter.Group3TestAdapter
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
class ThreeLevelGroupModel : ViewModel() {

    // 数据 -> item
    val groupList = GroupModel.getTwoGroupGroupOb(8, 3) // 普通列表数据

}