package me.lx.sample.model

import androidx.lifecycle.ViewModel
import me.lx.sample.group.adapter.GroupedListAdapter

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2020/1/17 14:06
 *  version: 1.0
 *  desc: 2级分组,child -> 包含child
 */
class TwoLevelGroupModel : ViewModel() {
    // 数据 -> item
    val groupAdapter = GroupedListAdapter()


}