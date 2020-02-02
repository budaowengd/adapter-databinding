package me.lx.sample.model

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.XmlItemBinding
import me.lx.rv.ext.itemBindingOf
import me.lx.rv.tools.Ls
import me.lx.sample.R
import me.lx.sample.vo.SingleItemVo

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2020/1/17 14:06
 *  version: 1.0
 *  desc:
 */
class SwitchSpanModel : ViewModel() {
    // 数据 -> item
    val singleItems = ObservableArrayList<SingleItemVo>().apply {
        for (i in 0 until 3) {
            add(SingleItemVo(i))
        }
    }
    // 适配器
    val adapter = BindingRecyclerViewAdapter<SingleItemVo>()

    val simpleItemBinding = itemBindingOf<SingleItemVo>(R.layout.item_single)
    val simpleGridItemBinding = itemBindingOf<SingleItemVo>(R.layout.item_single_grid)


    val layout = ObservableInt(1)

    //

    fun getXmlItem(): XmlItemBinding<SingleItemVo> {
        Ls.d("getXmlItem()...1111......layout=${layout.get()}")
        if (layout.get() == 1) {
            return simpleItemBinding
        }
        return simpleGridItemBinding
    }
}