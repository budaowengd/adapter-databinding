package me.lx.rv.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.ItemBinding
import me.lx.rv.R
import me.lx.rv.collections.AsyncDiffObservableList
import me.lx.rv.loadmore.LoadMoreWrapper

// RecyclerView
@BindingAdapter(
    value = ["rv_itemBinding", "rv_items", "rv_adapter", "rv_itemIds", "rv_viewHolder", "rv_diffConfig", "rv_support_loadmore_adapter"],
    requireAll = false
)
fun <T> setAdapter(
    recyclerView: RecyclerView,
    itemBinding: ItemBinding<T>?,
    items: List<T>?,
    rvAdapter: BindingRecyclerViewAdapter<T>?,
    itemIds: BindingRecyclerViewAdapter.ItemIds<in T>?,
    viewHolderFactory: BindingRecyclerViewAdapter.ViewHolderFactory?,
    diffConfig: AsyncDifferConfig<T>?
    , isSupportLoadMore: Boolean?) {

    println("BindingAdapter()..rv_setAdapter()....1111111111111....rvAdapter=${rvAdapter.hashCode()}.")
    if (recyclerView.adapter == rvAdapter) return
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
        if (isSupportLoadMore == true) {
            setLoadMoreAdapter(recyclerView, adapter)
        } else {
            recyclerView.adapter = adapter
        }
    }
}

@BindingAdapter(
    value = ["rv_support_loadmore_adapter"],
    requireAll = false
)
fun setLoadMoreAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
    println("BindingAdapter().. rv_support_loadmore()....2222222......")
    LoadMoreWrapper.with(adapter)
        .setFooterView(R.layout.base_footer)
        .setLoadFailedView(R.layout.base_load_failed)
        .setNoMoreView(R.layout.base_no_more)
        .setShowNoMoreEnabled(true)
        .setListener {
            println("加载更多回调..()....请求服务器了.......")
        }
        .into(recyclerView)
}

