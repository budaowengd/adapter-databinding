//package me.lx.rv
//
//import androidx.databinding.ObservableBoolean
//import androidx.lifecycle.ViewModel
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import me.lx.rv.loadmore.LoadMoreAdapter
//
///**
// *  author: luoXiong
// *  e-mail: 382060748@qq.com
// *  date: 2019/11/11 14:06
// *  version: 1.0
// *  desc:
// */
//interface RvBindListener<T> {
//    /**
//     * 获取列表分割线
//     */
//    fun getItemDecoration(): RecyclerView.ItemDecoration? {
//        return null
//    }
//
//    /**
//     * 获取列表适配器
//     * 给RecyclerView添加Adpter，请使用自定义的Adapter继承BindingRecyclerViewAdapter，重写onBindBinding方法，里面有你要的Item对应的binding对象。
//    Adapter属于View层的东西, 不建议定义到ViewModel中绑定，以免内存泄漏
//     */
//    fun getAdapter(): BindingRecyclerViewAdapter<T>? {
//        return null
//    }
//
//    /**
//     * 获取是否正在刷新中
//     */
//    fun getSpanSizeLookup(): GridLayoutManager.SpanSizeLookup? {
//        return null
//    }
//
//    /**
//     * 如果是:2 , 说明是 grid_span in 2..11
//     * 如果是:grid_span >= 12 , 说明是 StaggeredGridLayoutManager,  grid_span % 10
//     * 获取布局管理器
//     */
//    fun getLayoutFlag(): Int {
//        return RecyclerView.VERTICAL
//    }
//
//    /**
//     * 获取加载更多的监听
//     */
//    fun getLoadMoreListener(): LoadMoreAdapter.LoadMoreListener? {
//        return null
//    }
//}