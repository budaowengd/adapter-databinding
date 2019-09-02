package me.lx.sample

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/8/28 13:57
 *  version: 1.0
 *  desc:
 */
class MutableViewModel : ViewModel() {
    // 数据列表
    val items = ObservableArrayList<MutableItemVo>().apply {
        for (i in 0 until 3) {
            add(MutableItemVo(i))
        }
    }

    fun f1() {
        val aa: RecyclerView
    }

}