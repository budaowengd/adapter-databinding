package me.lx.sample

import android.os.Handler
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.OnItemBind
import me.lx.rv.XmlItemBinding
import me.lx.rv.click.BaseRvFun1ItemClickEvent
import me.lx.rv.collections.MergeObservableList
import me.lx.rv.ext.itemBindingOf
import me.lx.rv.ext.map
import me.lx.rv.itembindings.OnItemBindClass
import me.lx.rv.loadmore.LoadMoreAdapter
import me.lx.sample.vo.*

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/8/28 13:57
 *  version: 1.0
 *  desc:
 */
class MutableViewModel : ViewModel(), ClickListeners {
    // 加载更多相关
    var isNoMoreData = ObservableBoolean() // 加载更多后,没有更多数据的标识
    var isLoadMoreFailOb = ObservableBoolean() // 加载更多是否请求失败的标识
    var isLoadMoreFail = false // 模拟加载更多失败的标识,没有其他作用


    val loadMoreListener = object : LoadMoreAdapter.LoadMoreListener {
        override fun onLoadingMore() {
            println("onLoadingMore()..请求网络..22222....当前size=${singleItems.size} isLoadMoreFail=$isLoadMoreFail")
            Handler().postDelayed({
               isLoadMoreFailOb.set(isLoadMoreFail)
                if (!isLoadMoreFail) {
                    for (i in singleItems.size until singleItems.size + 3) {
                        singleItems.add(SingleItemVo(i))
                    }
                }
            }, 500)
        }

        override fun isShowNoMoreDataOb(): ObservableBoolean {
            return isNoMoreData
        }

        override fun isShowLoadMoreFailOb(): ObservableBoolean {
            return isLoadMoreFailOb
        }
    }

    // 适配器
    val adapter = BindingRecyclerViewAdapter<SingleItemVo>()
    val multiAdapter = BindingRecyclerViewAdapter<Any>()


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
        .insertItem(HeaderVo("Header"))
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


    fun t1() {
        val aa: RecyclerView? = null
        aa?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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