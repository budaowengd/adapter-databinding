package me.lx.rv.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.R
import me.lx.rv.XmlItemBinding
import me.lx.rv.collections.AsyncDiffObservableList
import me.lx.rv.group.GroupedRecyclerViewAdapter
import me.lx.rv.itembindings.OnItemBindClass
import me.lx.rv.loadmore.LoadMoreAdapter

/*
fun <T> set_rv_Adapter(recyclerView: RecyclerView, rv_layoutmanager: RecyclerView.LayoutManager? = null,
                       itemBinding: XmlItemBinding<T>?,
                       items: List<T>?,
                       rvAdapter: BindingRecyclerViewAdapter<T>?, itemIds: BindingRecyclerViewAdapter.ItemIds<in T>?,
                       viewHolderFactory: BindingRecyclerViewAdapter.ViewHolderFactory?,
                       diffConfig: AsyncDifferConfig<T>?, loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null) {
 */

// RecyclerView
@BindingAdapter(value = ["rv_layoutmanager", "rv_itemBinding", "rv_items", "rv_adapter", "rv_itemIds", "rv_viewHolder",
    "rv_diffConfig", "rv_loadmore_listener"], requireAll = false)
fun <T> set_rv_Adapter(recyclerView: RecyclerView, rv_layoutmanager: RecyclerView.LayoutManager? = null,
                       itemBindingAny: Any?,
                       items: List<T>?,
                       rvAdapter: BindingRecyclerViewAdapter<T>?, itemIds: BindingRecyclerViewAdapter.ItemIds<in T>?,
                       viewHolderFactory: BindingRecyclerViewAdapter.ViewHolderFactory?,
                       diffConfig: AsyncDifferConfig<T>?, loadMoreListener: LoadMoreAdapter.LoadMoreListener? = null) {
    // println("BindingAdapter()..rv_setAdapter()....1111111111111....rvAdapter=${rvAdapter.hashCode()}...  rvAp=${recyclerView.adapter?.hashCode()}")
    // XmlItemBinding OnItemBindClass
    if (recyclerView.adapter != null) return

    var itemBinding = itemBindingAny
    if (itemBinding is OnItemBindClass<*>) {
        itemBinding = XmlItemBinding.of(itemBinding)
    }
    if (items == null) return
    if (itemBinding !is XmlItemBinding<*>) return
    //requireNotNull(itemBinding) { "itemBinding must not be null" }
    var adapter = rvAdapter
    val oldAdapter = recyclerView.adapter as? BindingRecyclerViewAdapter<T>
    if (adapter == null) {
        if (oldAdapter == null) {
            adapter = BindingRecyclerViewAdapter()
        } else {
            adapter = oldAdapter
        }
    }
    adapter.setItemBinding(itemBinding as XmlItemBinding<T>)

    if (diffConfig != null) {
        var list: AsyncDiffObservableList<T>? = recyclerView.getTag(R.id.bindingcollectiondapter_list_id) as AsyncDiffObservableList<T>
        if (list == null) {
            list = AsyncDiffObservableList(diffConfig)
            recyclerView.setTag(R.id.bindingcollectiondapter_list_id, list)
            adapter.setItems(list)
        }
        list.update(items)
    } else {
        adapter.setItems(items)
    }

    adapter.setItemIds(itemIds)
    adapter.setViewHolderFactory(viewHolderFactory)

    if (oldAdapter != adapter) {
        recyclerView.layoutManager = rv_layoutmanager ?: LinearLayoutManager(recyclerView.context)
        if (loadMoreListener != null) {
            recyclerView.adapter = getLoadMoreAdapter(adapter, loadMoreListener)
        } else {
            recyclerView.adapter = adapter
        }
    }
}

@BindingAdapter(value = ["rv_group_adapter", "rv_group_items", "rv_group_chick_child_listener",
    "rv_group_chick_header_listener", "rv_group_chick_footer_listener", "rv_group_rv_layoutManager"], requireAll = false)
fun <T, C> set_rv_GroupAdapter(recyclerView: RecyclerView, adapter: GroupedRecyclerViewAdapter<T, C>?,
                               items: List<T>?, clickChildListener: GroupedRecyclerViewAdapter.ClickGroupListener? = null,
                               clickHeaderListener: GroupedRecyclerViewAdapter.ClickGroupListener? = null,
                               clickFooterListener: GroupedRecyclerViewAdapter.ClickGroupListener? = null,
                               rv_layoutManager: RecyclerView.LayoutManager? = null) {
//    println("set_rv_GroupAdapter().1111..adapter=$adapter")
    if (recyclerView.adapter != null) return
    if(items==null)return
    if(adapter==null)return
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
    if (rv_layoutManager == null) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    } else {
        recyclerView.layoutManager = rv_layoutManager
    }
    recyclerView.adapter = adapter

}

fun getLoadMoreAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, loadMoreListener: LoadMoreAdapter.LoadMoreListener): LoadMoreAdapter {
    val loadMoreAdapter = LoadMoreAdapter.with(adapter).listenerNoMoreDataAndFail(loadMoreListener)
    return loadMoreAdapter
}

