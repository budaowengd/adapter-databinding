package me.lx.sample

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.ItemBinding
import me.lx.rv.OnItemBind
import me.lx.rv.click.BaseItemClickEvent
import me.lx.rv.collections.MergeObservableList
import me.lx.rv.ext.itemBindingOf
import me.lx.rv.ext.map
import me.lx.rv.itembindings.OnItemBindClass
import me.lx.sample.vo.*

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/8/28 13:57
 *  version: 1.0
 *  desc:
 */
class MutableViewModel : ViewModel(), ClickListeners {

    val adapter = BindingRecyclerViewAdapter<SingleItemVo>()
    val multiAdapter = BindingRecyclerViewAdapter<Any>()

    private val itemClickEvent = object : BaseItemClickEvent<Any>() {
        override fun onItemClick(item: Any) {
            (item as? SingleItemVo)?.onToggleChecked()
        }
    }

    // 数据 -> item
    val singleItems = ObservableArrayList<SingleItemVo>().apply {
        for (i in 0 until 3) {
            add(SingleItemVo(i))
        }
    }

    //  数据 -> 包含头部、item、底部
    val headerFooterItems = MergeObservableList<Any>()
        .insertItem(HeaderVo("Header"))
        .insertList(singleItems)
        .insertItem(FooterVo("Footer11"))
        .insertItem(FooterVo("Footer22"))

    //  数据 -> 多类型
    val multiItems = MergeObservableList<Any>()
        .insertItem(Type1Vo("type1-0"))
        .insertItem(Type2Vo("type2-0"))
        .insertList(singleItems)
        .insertItem(Type1Vo("type1-1"))
        .insertItem(Type2Vo("type2-1"))

    // 布局 ->单一的
    val simpleItemBinding = itemBindingOf<SingleItemVo>(BR.item, R.layout.item_single)
        .bindExtra(BR.click, itemClickEvent)

    // 布局 -> 多类型
    val multiItemBinding = OnItemBindClass<Any>().apply {
        map<SingleItemVo>(BR.item, R.layout.item_single)
        map<Type1Vo>(BR.item, R.layout.item_type_1)
        map<Type2Vo>(BR.item, R.layout.item_type_2)
    }

    // 布局 -> 带头和脚
    val headerFooterItemBinding = itemBindingOf<Any>(object : OnItemBind<Any> {
        override fun onItemBind(itemBinding: ItemBinding<*>, position: Int, item: Any) {
            when (item::class) {
                HeaderVo::class -> itemBinding.set(BR.item, R.layout.item_header)
                SingleItemVo::class -> itemBinding.set(BR.item, R.layout.item_single)
                FooterVo::class -> itemBinding.set(BR.item, R.layout.item_footer)
            }
        }
    })


    override fun clickAddItem() {
        singleItems.add(SingleItemVo(index = singleItems.size))
    }

    override fun clickRemoveItem() {
        if (singleItems.size > 1) {
            singleItems.removeAt(singleItems.size - 1)
        }
    }
}