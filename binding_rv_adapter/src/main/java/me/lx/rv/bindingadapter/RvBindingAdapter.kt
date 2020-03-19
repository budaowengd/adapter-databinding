package me.lx.rv.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.*
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.R
import me.lx.rv.XmlItemBinding
import me.lx.rv.collections.AsyncDiffObservableList
import me.lx.rv.group.ClickGroupListener
import me.lx.rv.group.GroupedRecyclerViewAdapter
import me.lx.rv.group.ThreeLevelGroupedRecyclerViewAdapter
import me.lx.rv.itembindings.OnItemBindClass
import me.lx.rv.loadmore.LoadMoreAdapter
import me.lx.rv.tools.Ls

/*
fun <T> set_rv_Adapter(recyclerView: RecyclerView, rv_layoutmanager: RecyclerView.LayoutManager? = null,
                       itemBinding: XmlItemBinding<T>?,
                       items: List<T>?,
                       rvAdapter: BindingRecyclerViewAdapter<T>?, itemIds: BindingRecyclerViewAdapter.ItemIds<in T>?,
                       viewHolderFactory: BindingRecyclerViewAdapter.ViewHolderFactory?,
                       diffConfig: AsyncDifferConfig<T>?, loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null) {
 */

// RecyclerView "rv_layoutmanager", "rv_layout_is_horizontal"
// RecyclerView
@BindingAdapter(
    value = ["rv_itemBinding", "rv_items", "rv_adapter", "rv_itemIds", "rv_viewHolderFactory",
        "rv_diffConfig", "rv_loadmore_listener", "rv_layout_span", "rv_SpanSizeLookup"],
    requireAll = false
)
fun <T> set_rv_Adapter(
    rv: RecyclerView,
    xmlAny: Any?,
    items: List<T>?,
    pAdapter: BindingRecyclerViewAdapter<T>?,
    itemIds: BindingRecyclerViewAdapter.ItemIds<in T>?,
    viewHolderFactory: BindingRecyclerViewAdapter.ViewHolderFactory?,
    diffConfig: AsyncDifferConfig<T>?,
    loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null,
    grid_span: Int = 1,
    spanLookup: GridLayoutManager.SpanSizeLookup? = null
) {
    Ls.d("BindingAdapter()..rv_setAdapter()....1111111111111....pAdapter=${pAdapter.hashCode()}  rvAp=${rv.adapter?.hashCode()}")
    if (rv.adapter != null) return
    if (items == null) return
    if (xmlAny == null) return

//    rv.setRecycledViewPool()
//    rvAdapter.setLifecycleOwner()
    var xmlItem = xmlAny
    if (xmlItem is OnItemBindClass<*>) {
        xmlItem = XmlItemBinding.of(xmlItem)
    }
    if (xmlItem !is XmlItemBinding<*>) return

    var newAdapter = pAdapter
    val oldAdapter = rv.adapter
    if (newAdapter == null) {
        if (oldAdapter is BindingRecyclerViewAdapter<*>) {
            newAdapter = oldAdapter as BindingRecyclerViewAdapter<T>
        } else {
            newAdapter = BindingRecyclerViewAdapter()
        }
    }
    newAdapter.init()
    newAdapter.setItemBinding(xmlItem as XmlItemBinding<T>)
    set_rv_layoutmanager(rv, grid_span, spanLookup)
    if (diffConfig != null) {
        var list: AsyncDiffObservableList<T>? =
            rv.getTag(R.id.bindingcollectiondapter_list_id) as AsyncDiffObservableList<T>
        if (list == null) {
            list = AsyncDiffObservableList(diffConfig)
            rv.setTag(R.id.bindingcollectiondapter_list_id, list)
            newAdapter.setItems(list)
        }
        list.update(items)
    } else {
        newAdapter.setItems(items)
    }
    newAdapter.setItemIds(itemIds)
    newAdapter.setViewHolderFactory(viewHolderFactory)
    if (oldAdapter != newAdapter) {
        if (loadMoreListener != null) {
            rv.adapter = getLoadMoreAdapter(newAdapter, loadMoreListener)
        } else {
            rv.adapter = newAdapter
        }
    }
}

/**
 * 不能使用BindingAdapter的原因是,这里会比setAdapter后执行,在loadmoreAdapter里获取不到Lookup对象
 * , "rv_group_layoutmanager"
 *  rv_layoutmanager  rv_layout_is_horizontal
 * @param layoutHorizontal
 * @param grid_span 如果是1, 创建 LinearLayout 垂直方向
 *          如果==0,会创建 LinearLayout,并设置 水平方向
 *          如果大于1,会创建 GridLayout,并设置spanCount
 *          如果是负数,不设置layout
 *          如果大于等于12,说明是瀑布流布局,取个位数作为spanCount
 *          GridLayoutManager.SpanSizeLookup
 */
//@BindingAdapter(value = ["rv_layout_span", "rv_SpanSizeLookup"], requireAll = false)
fun set_rv_layoutmanager(
    rv: RecyclerView,
    grid_span: Int = 1,
    spanLookup: GridLayoutManager.SpanSizeLookup? = null
) {
    //Ls.d("set_rv_layoutmanager().....grid_span=$grid_span")

    var layout: RecyclerView.LayoutManager? = null
    if (grid_span == 0 || grid_span == 1) {
        layout = LinearLayoutManager(rv.context, if (grid_span == 0) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL, false)
    } else if (grid_span in 2..11) {
        layout = GridLayoutManager(rv.context, grid_span)
        if (spanLookup != null) {
            layout.spanSizeLookup = spanLookup
        }
    } else if (grid_span >= 12) {
        val span = grid_span % 10
        layout = StaggeredGridLayoutManager(span, RecyclerView.VERTICAL)
    }
    if (layout != null) {
        rv.layoutManager = layout
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
    grid_span: Int = 1,
    spanLookup: GridLayoutManager.SpanSizeLookup? = null,
    loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null,
    clickChildListener: ClickGroupListener? = null,
    clickHeaderListener: ClickGroupListener? = null,
    clickFooterListener: ClickGroupListener? = null
) {
//    Ls.d("set_rv_GroupAdapter().1111..adapter=$adapter")
    if (rv.adapter != null) return
    if (items == null) return
    if (adapter == null) return
    adapter.setGroupList(items)
    set_rv_layoutmanager(rv, if (grid_span == 0) 1 else grid_span, spanLookup)
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
    value = ["rv_two_level_group_adapter", "rv_two_level_group_items",
        "rv_two_level_group_layout_span", "rv_two_level_group_SpanSizeLookup",
        "rv_two_level_group_loadmore_listener", "rv_two_level_group_chick_child_child_listener",
        "rv_two_level_group_chick_header_listener", "rv_two_level_group_chick_footer_listener"],
    requireAll = false
)
fun <G, CG, CC> set_rv_two_level_GroupAdapter(
    rv: RecyclerView, adapter: ThreeLevelGroupedRecyclerViewAdapter<G, CG, CC>?,
    items: List<G>?,
    grid_span: Int = 1,
    spanLookup: GridLayoutManager.SpanSizeLookup? = null,
    loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null,
    clickChildChildListener: ClickGroupListener? = null,
    clickHeaderListener: ClickGroupListener? = null,
    clickFooterListener: ClickGroupListener? = null
) {
//    Ls.d("set_rv_GroupAdapter().1111..adapter=$adapter")
    if (rv.adapter != null) return
    if (items == null) return
    if (adapter == null) return
    adapter.setGroupList(items)
    set_rv_layoutmanager(rv, if (grid_span == 0) 1 else grid_span, spanLookup)
    if (clickChildChildListener != null) {
        adapter.setClickChildChildListener(clickChildChildListener)
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


fun getLoadMoreAdapter(
    adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    loadMoreListener: LoadMoreAdapter.LoadMoreListener
): LoadMoreAdapter {
    val loadMoreAdapter = LoadMoreAdapter.with(adapter).listenerNoMoreDataAndFail(loadMoreListener)
    return loadMoreAdapter
}

