package me.lx.sample.model

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.click.BaseRvFun1ItemClickEvent
import me.lx.rv.ext.itemBindingOf
import me.lx.sample.R
import me.lx.sample.divider.GridSupportMatchAvgDivider
import me.lx.sample.vo.SingleItemVo

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2020/1/17 14:06
 *  version: 1.0
 *  desc:
 */
class GridDividerModel : ViewModel() {
    //    var gridDivider = object : GridSpaceDividerSupportMatch() {
//        override fun getAvgGap(): Int {
//            return 12
//        }
//
//        override fun getTopGap(): Int {
//            return 16
//        }
//    }
    private val divider = GridSupportMatchAvgDivider().setMarginTop(12).setAvgGap(12)

    fun getDivider(): RecyclerView.ItemDecoration? {
        return divider // null divider
    }

    val gridAdapter = BindingRecyclerViewAdapter<SingleItemVo>()

    val grid3Items = ObservableArrayList<SingleItemVo>().apply {
        for (i in 0 until 12) {
            add(SingleItemVo(i))
        }
    }
    val grid3Xml = itemBindingOf<SingleItemVo>(
        R.layout.item_grid3,
        object : BaseRvFun1ItemClickEvent<SingleItemVo>() {
            override fun clickRvItem(item: SingleItemVo) {
                item.isCheckedOb.set(!item.isCheckedOb.get())
            }
        })
}