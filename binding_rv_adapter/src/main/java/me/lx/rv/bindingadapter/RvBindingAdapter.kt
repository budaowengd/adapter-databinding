package me.lx.rv.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.R
import me.lx.rv.XmlItemBinding
import me.lx.rv.click.ClickListener
import me.lx.rv.ext.itemXmlOf
import me.lx.rv.group.GroupedRecyclerViewAdapter
import me.lx.rv.group.Group3RecyclerViewAdapter
import me.lx.rv.itembindings.OnItemBindClass
import me.lx.rv.loadmore.LoadMoreAdapter
import me.lx.rv.tools.Ls
import me.lx.rv.tools.RvUtils

/*
fun <T> set_rv_Adapter(recyclerView: RecyclerView, rv_layoutmanager: RecyclerView.LayoutManager? = null,
                       itemBinding: XmlItemBinding<T>?,
                       items: List<T>?,
                       rvAdapter: BindingRecyclerViewAdapter<T>?, itemIds: BindingRecyclerViewAdapter.ItemIds<in T>?,
                       viewHolderFactory: BindingRecyclerViewAdapter.ViewHolderFactory?,
                       diffConfig: AsyncDifferConfig<T>?, loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null) {
 */
// 分割线如果比LayoutManager或者SpanSizeLookup先设置,会出现布局错乱
// 因为分割线里需要获取LayoutManager,进行判断
// 所以分割线的设置要在LayoutManager 后面

// RecyclerView "rv_layoutmanager", "rv_layout_is_horizontal"
// RecyclerView
@BindingAdapter(
    value = ["rv_adapter", "rv_itemBinding", "rv_items", "rv_itemIds", "rv_viewHolderFactory",
        "rv_loadmore_listener", "rv_item_decoration", "rv_item_clickEvent", "rv_layout_span", "rv_SpanSizeLookup"],
    requireAll = false
)
fun <T> set_rv_Adapter(
    rv: RecyclerView,
    adapter: BindingRecyclerViewAdapter<T>?,
    xmlAny: Any?,
    items: List<T>?,
    itemIds: BindingRecyclerViewAdapter.ItemIds<in T>?,
    viewHolderFactory: BindingRecyclerViewAdapter.ViewHolderFactory?,
    loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null,
    divider: RecyclerView.ItemDecoration?,
    itemClick: ClickListener? = null,
    grid_span: Int? = null,
    spanLookup: GridLayoutManager.SpanSizeLookup? = null
) {
//    Ls.d("BindingAdapter()..rv_setAdapter()....1111111111111... rvAp=${rv.adapter?.hashCode()}")
    if (rv.adapter != null) return
    if (items == null) return
    if (xmlAny == null) return
//    rv.setRecycledViewPool()
//    rvAdapter.setLifecycleOwner()
    var xmlItem = xmlAny
    if (xmlAny is OnItemBindClass<*>) {
        xmlItem = XmlItemBinding.of(xmlAny)
    } else if (xmlAny is Int) {
        xmlItem = itemXmlOf<Any>(xmlAny)
    }
    if (xmlItem !is XmlItemBinding<*>) return
    if (itemClick != null) {
        xmlItem.setClickEvent(itemClick)
    }
    var newAdapter = adapter
    if (newAdapter == null) {
        newAdapter = BindingRecyclerViewAdapter()
    }
    RvUtils.set_rv_layoutmanager(rv, grid_span, spanLookup)
    RvUtils.set_rv_divider(rv, divider)
    newAdapter.init()
    newAdapter.setItemBinding(xmlItem as XmlItemBinding<T>)

//    if (diffConfig != null) {
//        var list: AsyncDiffObservableList<T>? =
//            rv.getTag(R.id.bindingcollectiondapter_list_id) as AsyncDiffObservableList<T>
//        if (list == null) {
//            list = AsyncDiffObservableList(diffConfig)
//            rv.setTag(R.id.bindingcollectiondapter_list_id, list)
//            newAdapter.setItems(list)
//        }
//        list.update(items)
    newAdapter.setItems(items)
    newAdapter.setItemIds(itemIds)
    newAdapter.setViewHolderFactory(viewHolderFactory)
    if (loadMoreListener != null && newAdapter !is LoadMoreAdapter) {
        rv.adapter = getLoadMoreAdapter(newAdapter, loadMoreListener)
    } else {
        rv.adapter = newAdapter
    }
}


//
@BindingAdapter(
    value = ["rv_group_adapter", "rv_group_items",
        "rv_group_layout_span", "rv_group_SpanSizeLookup",
        "rv_group_loadmore_listener", "rv_group_chick_child_listener",
        "rv_group_chick_header_listener", "rv_group_chick_footer_listener"],
    requireAll = false
)
fun <T, C> set_rv_GroupAdapter(
    rv: RecyclerView, adapter: GroupedRecyclerViewAdapter<T, C>?,
    items: List<T>?,
    grid_span: Int?,
    spanLookup: GridLayoutManager.SpanSizeLookup? = null,
    loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null,
    clickChildListener: ClickListener? = null,
    clickHeaderListener: ClickListener? = null,
    clickFooterListener: ClickListener? = null
) {
    Ls.d("set_rv_GroupAdapter().1111..adapter=$adapter  rv.adapter=${rv.adapter}")
    if (rv.adapter != null) return
    if (items == null) return
    if (adapter == null) return
    RvUtils.set_rv_layoutmanager(rv, grid_span, spanLookup)
    adapter.setGroupList(items)
    if (clickChildListener != null) {
        adapter.setClickChildListener(clickChildListener)
    }
    if (clickHeaderListener != null) {
        adapter.setClickHeaderListener(clickHeaderListener)
    }
    if (clickFooterListener != null) {
        adapter.setClickFooterListener(clickFooterListener)
    }
    if (loadMoreListener != null) {
        rv.adapter = getLoadMoreAdapter(adapter, loadMoreListener)
    } else {
        rv.adapter = adapter
    }
}


@BindingAdapter(
    value = ["rv_group3_adapter", "rv_group3_items",
        "rv_group3_layout_span", "rv_group3_SpanSizeLookup",
        "rv_group3_chick_cc_listener",
        "rv_group3_chick_header_listener", "rv_group3_chick_footer_listener",
        "rv_group3_chick_cg_header_footer"
    ],
    requireAll = false
)
fun <G, CG, CC> set_rv_three_level_GroupAdapter(
    rv: RecyclerView, adapter: Group3RecyclerViewAdapter<G, CG, CC>?,
    items: List<G>?,
    grid_span: Int? = null,
    spanLookup: GridLayoutManager.SpanSizeLookup? = null,
    clickCcListener: ClickListener? = null,
    clickHeaderListener: ClickListener? = null,
    clickFooterListener: ClickListener? = null,
    cgHeaderFooterClick: ClickListener? = null
) {
//    Ls.d("set_rv_GroupAdapter().1111..adapter=$adapter")
    if (rv.adapter != null) return
    if (items == null) return
    if (adapter == null) return
    RvUtils.set_rv_layoutmanager(rv, grid_span, spanLookup)
    adapter.setGroupList(items)
    if (clickCcListener != null) {
        adapter.setClickCcListener(clickCcListener)
    }
    if (clickHeaderListener != null) {
        adapter.setClickHeaderListener(clickHeaderListener)
    }
    if (clickFooterListener != null) {
        adapter.setClickFooterListener(clickFooterListener)
    }
    if (cgHeaderFooterClick != null) {
        adapter.setClickCgHeaderFooterListener(cgHeaderFooterClick)
    }

    rv.adapter = adapter
}


fun getLoadMoreAdapter(
    adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    loadMoreListener: LoadMoreAdapter.LoadMoreListener
): LoadMoreAdapter {
    val loadMoreAdapter = LoadMoreAdapter.with(adapter).listenerNoMoreDataAndFail(loadMoreListener)
    return loadMoreAdapter
}

