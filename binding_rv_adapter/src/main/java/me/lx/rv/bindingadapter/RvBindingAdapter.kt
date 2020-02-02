package me.lx.rv.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.*
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.R
import me.lx.rv.XmlItemBinding
import me.lx.rv.collections.AsyncDiffObservableList
import me.lx.rv.group.GroupedRecyclerViewAdapter
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
        "rv_diffConfig", "rv_loadmore_listener"],
    requireAll = false
)
fun <T> set_rv_Adapter(
    rv: RecyclerView,
    xmlAny: Any,
    items: List<T>,
    rvAdapter: BindingRecyclerViewAdapter<T>?,
    itemIds: BindingRecyclerViewAdapter.ItemIds<in T>?,
    viewHolderFactory: BindingRecyclerViewAdapter.ViewHolderFactory?,
    diffConfig: AsyncDifferConfig<T>?,
    loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null
) {
    Ls.d("BindingAdapter()..rv_setAdapter()....1111111111111....rvAdapter=${rvAdapter.hashCode()}...  rvAp=${rv.adapter?.hashCode()}")
    if (rv.adapter != null) return

    var xmlItem = xmlAny
    if (xmlItem is OnItemBindClass<*>) {
        xmlItem = XmlItemBinding.of(xmlItem)
    }
    if (xmlItem !is XmlItemBinding<*>) return
    var adapter = rvAdapter
    val oldAdapter = rv.adapter
    if (adapter == null) {
        if (oldAdapter == null) {
            adapter = BindingRecyclerViewAdapter()
        } else {
            adapter = oldAdapter as BindingRecyclerViewAdapter<T>
        }
    }
    adapter.setItemBinding(xmlItem as XmlItemBinding<T>)

    if (diffConfig != null) {
        var list: AsyncDiffObservableList<T>? =
            rv.getTag(R.id.bindingcollectiondapter_list_id) as AsyncDiffObservableList<T>
        if (list == null) {
            list = AsyncDiffObservableList(diffConfig)
            rv.setTag(R.id.bindingcollectiondapter_list_id, list)
            adapter.setItems(list)
        }
        list.update(items)
    } else {
        adapter.setItems(items)
    }
    adapter.setItemIds(itemIds)
    adapter.setViewHolderFactory(viewHolderFactory)
    if (oldAdapter != adapter) {
        if (loadMoreListener != null) {
            rv.adapter = getLoadMoreAdapter(adapter, loadMoreListener)
        } else {
            rv.adapter = adapter
        }
    }
}

/**
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
@BindingAdapter(value = ["rv_layout_span", "rv_SpanSizeLookup"], requireAll = false)
fun set_rv_layoutmanager(
    rv: RecyclerView,
    grid_span: Int = 1,
    spanLookup: GridLayoutManager.SpanSizeLookup? = null
) {
    Ls.d("set_rv_layoutmanager().....grid_span=$grid_span")
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
        "rv_group_loadmore_listener", "rv_group_chick_child_listener", "rv_group_chick_header_listener", "rv_group_chick_footer_listener"],
    requireAll = false
)
fun <T, C> set_rv_GroupAdapter(
    recyclerView: RecyclerView, adapter: GroupedRecyclerViewAdapter<T, C>?,
    items: List<T>?
    , loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null
    , clickChildListener: GroupedRecyclerViewAdapter.ClickGroupListener? = null
    , clickHeaderListener: GroupedRecyclerViewAdapter.ClickGroupListener? = null
    , clickFooterListener: GroupedRecyclerViewAdapter.ClickGroupListener? = null

) {
//    Ls.d("set_rv_GroupAdapter().1111..adapter=$adapter")
    if (recyclerView.adapter != null) return
    if (items == null) return
    if (adapter == null) return
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
        recyclerView.adapter = getLoadMoreAdapter(adapter, loadMoreListener)
    } else {
        recyclerView.adapter = adapter
    }

}

fun getLoadMoreAdapter(
    adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    loadMoreListener: LoadMoreAdapter.LoadMoreListener
): LoadMoreAdapter {
    val loadMoreAdapter = LoadMoreAdapter.with(adapter).listenerNoMoreDataAndFail(loadMoreListener)
    return loadMoreAdapter
}

