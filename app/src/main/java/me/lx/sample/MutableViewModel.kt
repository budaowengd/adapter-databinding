package me.lx.sample

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.OnItemBind
import me.lx.rv.XmlItemBinding
import me.lx.rv.click.BaseRvFun1ItemClickEvent
import me.lx.rv.collections.MergeObservableList
import me.lx.rv.ext.itemBindingOf
import me.lx.rv.ext.map
import me.lx.rv.itembindings.OnItemBindClass
import me.lx.rv.tools.Ls
import me.lx.sample.divider.BaseThreeGridItemDecoration
import me.lx.sample.divider.GridSpaceDividerSupportMatch
import me.lx.sample.vo.*

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/8/28 13:57
 *  version: 1.0
 *  desc:
 */
class MutableViewModel : ViewModel(), ClickListeners {




    // 适配器
    val adapter = BindingRecyclerViewAdapter<SingleItemVo>()

    val multiAdapter = BindingRecyclerViewAdapter<Any>()

    // grid3
    val gridAdapter = BindingRecyclerViewAdapter<SingleItemVo>()
    val rightLayoutManager = GridLayoutManager(MyApp.sContext, 3)
    val grid3Items = ObservableArrayList<SingleItemVo>().apply {
        for (i in 0 until 12) {
            add(SingleItemVo(i))
        }
    }
    val grid3Xml = itemBindingOf<SingleItemVo>(R.layout.item_grid3,
        object : BaseRvFun1ItemClickEvent<SingleItemVo>() {
            override fun clickRvItem(item: SingleItemVo) {
                item.isCheckedOb.set(!item.isCheckedOb.get())
            }
        })
    val gridDivider2 = object : BaseThreeGridItemDecoration(rightLayoutManager) {
        override fun getLeftRightGap(): Int {
            return 10
        }

        override fun isHaveHeadLayout(): Boolean {
            return false
        }

        override fun getMiddleGap(): Int {
            return 10
        }

    }
    // var gridDivider = object : GridSpaceDivider(rightLayoutManager) {
    var gridDivider = object : GridSpaceDividerSupportMatch() {
        override fun getAvgGap(): Int {
            return 12
        }

        override fun getTopGap(): Int {
            return 16
        }
    }


    // 点击事件
    private val itemClickEvent = object : BaseRvFun1ItemClickEvent<Any>() {
        override fun clickRvItem(item: Any) {
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
        .insertItem(Header2Vo())
        .insertList(singleItems)
        .insertItem(FooterVo("Footer11"))
        .insertItem(FooterVo("Footer22"))

    //  数据 -> 多类型 d d
    val multiItems = MergeObservableList<Any>()
        .insertItem(Type1Vo("type1-0"))
        .insertItem(Type2Vo("type2-0"))
        .insertList(singleItems)
        .insertItem(Type1Vo("type1-1"))
        .insertItem(Type2Vo("type2-1"))

    // 布局 ->单一的
    val simpleItemBinding = itemBindingOf<SingleItemVo>(R.layout.item_single, itemClickEvent)

    // 布局 ->单一的
    val simpleItemBinding2 = itemBindingOf<SingleItemVo>(R.layout.item_single, itemClickEvent)

    // 布局 -> 多类型
    val multiItemBinding = OnItemBindClass<Any>().apply {
        map<SingleItemVo>(R.layout.item_single, itemClickEvent)
        map<Type1Vo>(R.layout.item_type_1)
        map<Type2Vo>(R.layout.item_type_2, BR.item)
    }

    // 布局 -> 带头和脚
    val headerFooterItemBinding = itemBindingOf<Any>(object : OnItemBind<Any> {
        override fun onGetItemViewType(itemBinding: XmlItemBinding<*>, position: Int, item: Any) {
            when (item::class) {
                HeaderVo::class -> itemBinding.set(R.layout.item_header, BR.item, itemClickEvent)
                Header2Vo::class -> itemBinding.set(R.layout.item_header2, BR.item, itemClickEvent)
                SingleItemVo::class -> itemBinding.set(R.layout.item_single, itemClickEvent)
                FooterVo::class -> itemBinding.set(R.layout.item_footer, itemClickEvent)
            }
        }


    })

    val headerFooterItemBinding2 = OnItemBindClass<Any>().apply {
        map<HeaderVo>(R.layout.item_header, itemClickEvent)
        map<Header2Vo>(R.layout.item_header2)
        map<SingleItemVo>(R.layout.item_single)
        map<FooterVo>(R.layout.item_footer)
    }



    override fun clickAddItem() {
        singleItems.add(SingleItemVo(index = singleItems.size))

        val header = headerFooterItems.get(0) as? Header2Vo
        header?.name="我是${singleItems.size}"
        header?.nameOb?.set("我是${singleItems.size}")
        Ls.d("clickAddItem()...1111...singleItems=${singleItems.size}  header=${header?.name}")
        // multiAdapter.notifyDataSetChanged()
    }

    override fun clickRemoveItem() {
        if (singleItems.size > 1) {
            singleItems.removeAt(singleItems.size - 1)
        }
    }
}