package me.lx.sample

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.OnItemBind
import me.lx.rv.XmlItemBinding
import me.lx.rv.click.BaseItemClickEvent
import me.lx.rv.collections.MergeObservableList
import me.lx.rv.ext.itemBindingOf
import me.lx.rv.ext.map
import me.lx.rv.itembindings.OnItemBindClass
import me.lx.rv.loadmore22.LoadMoreWrapper2222
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

    fun a2(recyclerView:RecyclerView){
        LoadMoreWrapper2222.with(adapter)
            .setFooterView(me.lx.rv.R.layout.base_footer)
            .setLoadFailedView(me.lx.rv.R.layout.base_load_failed)
            .setNoMoreView(me.lx.rv.R.layout.base_no_more)
            .setShowNoMoreEnabled(true)
            .setListener {
                println("加载更多回调..()....请求服务器了.......")
            }
            .into(recyclerView)
    }

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
        override fun onItemBind(itemBinding: XmlItemBinding<*>, position: Int, item: Any) {
            when (item::class) {
                HeaderVo::class -> itemBinding.set(R.layout.item_header, BR.item, itemClickEvent)
                SingleItemVo::class -> itemBinding.set(R.layout.item_single, itemClickEvent)
                FooterVo::class -> itemBinding.set(R.layout.item_footer, itemClickEvent)
            }
        }
    })

    val headerFooterItemBinding2 = OnItemBindClass<Any>().apply {
        map<HeaderVo>(R.layout.item_header, itemClickEvent)
        map<SingleItemVo>(R.layout.item_single)
        map<FooterVo>(R.layout.item_footer)
    }

    fun t1(){
        val aa:RecyclerView?=null
        aa?.addOnScrollListener(object:RecyclerView.OnScrollListener(){

        })
    }

    override fun clickAddItem() {
        singleItems.add(SingleItemVo(index = singleItems.size))
    }

    override fun clickRemoveItem() {
        if (singleItems.size > 1) {
            singleItems.removeAt(singleItems.size - 1)
        }
    }
}