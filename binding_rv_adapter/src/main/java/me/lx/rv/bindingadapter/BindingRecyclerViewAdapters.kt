package me.lx.rv.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.R
import me.lx.rv.XmlItemBinding
import me.lx.rv.collections.AsyncDiffObservableList
import me.lx.rv.group.GroupedRecyclerViewAdapter
import me.lx.rv.loadmore.LoadMoreAdapter

// RecyclerView
@BindingAdapter(
    value = ["rv_itemBinding", "rv_items", "rv_adapter", "rv_itemIds", "rv_viewHolder", "rv_diffConfig", "rv_loadmore_listener"],
    requireAll = false
)
fun <T> setAdapter(
    recyclerView: RecyclerView,
    itemBinding: XmlItemBinding<T>?,
    items: List<T>?,
    rvAdapter: BindingRecyclerViewAdapter<T>?,
    itemIds: BindingRecyclerViewAdapter.ItemIds<in T>?,
    viewHolderFactory: BindingRecyclerViewAdapter.ViewHolderFactory?,
    diffConfig: AsyncDifferConfig<T>?,
    loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null
) {
   // println("BindingAdapter()..rv_setAdapter()....1111111111111....rvAdapter=${rvAdapter.hashCode()}...  rvAp=${recyclerView.adapter?.hashCode()}")
    if (recyclerView.adapter != null) return
    var adapter = rvAdapter
    requireNotNull(itemBinding) { "itemBinding must not be null" }
    val oldAdapter = recyclerView.adapter as? BindingRecyclerViewAdapter<T>
    if (adapter == null) {
        if (oldAdapter == null) {
            adapter = BindingRecyclerViewAdapter()
        } else {
            adapter = oldAdapter
        }
    }
    adapter.setItemBinding(itemBinding)

    if (diffConfig != null && items != null) {
        var list: AsyncDiffObservableList<T>? =
            recyclerView.getTag(R.id.bindingcollectiondapter_list_id) as AsyncDiffObservableList<T>
        if (list == null) {
            list = AsyncDiffObservableList(diffConfig)
            recyclerView.setTag(R.id.bindingcollectiondapter_list_id, list)
            adapter.setItems(list)
        }
        list.update(items)
    } else {
        adapter.setItems(items!!)
    }

    adapter.setItemIds(itemIds)
    adapter.setViewHolderFactory(viewHolderFactory)

    if (oldAdapter != adapter) {
        if (loadMoreListener != null) {
            recyclerView.adapter = getLoadMoreAdapter(adapter, loadMoreListener)
        } else {
            recyclerView.adapter = adapter
        }
    }
}

@BindingAdapter(
    value = ["rv_group_adapter", "rv_group_item_list", "rv_group_chick_child_listener"
        , "rv_group_chick_header_listener", "rv_group_chick_footer_listener", "rv_group_rv_layoutManager"],
    requireAll = false
)
fun <T, C> setGroupAdapter(
    recyclerView: RecyclerView,
    adapter: GroupedRecyclerViewAdapter<T, C>, items: List<T>,
    clickChildListener: GroupedRecyclerViewAdapter.ClickChildListener? = null,
    clickHeaderListener: GroupedRecyclerViewAdapter.ClickChildListener? = null,
    clickFooterListener: GroupedRecyclerViewAdapter.ClickChildListener? = null,
    rv_layoutManager: RecyclerView.LayoutManager? = null
) {
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
    if (rv_layoutManager != null) {
        recyclerView.layoutManager = rv_layoutManager
    }
    recyclerView.adapter = adapter

}

fun getLoadMoreAdapter(
    adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    loadMoreListener: LoadMoreAdapter.LoadMoreListener
): LoadMoreAdapter {
    val loadMoreAdapter = LoadMoreAdapter.with(adapter).setLoadMoreListener(loadMoreListener)
    return loadMoreAdapter
}

