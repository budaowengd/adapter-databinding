package me.lx.rv.loadmore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.lx.rv.ext.itemBindingOf


/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/6 09:01
 *  version: 1.0
 *  desc:
 */
class LoadMoreAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private var mPageSize = 0
    private var mInflater: LayoutInflater? = null
    @LayoutRes
    private var mLoadMoreRedId = me.lx.rv.R.layout.rv_load_more_layout

    private var mLoadMoreView: View? = null

    private var mIsLoading: Boolean = false

    private var mLoadMoreListener: LoadMoreListener? = null
    private var mLoadMoreFailClickListener: LoadMoreFailClickListener? = null

    // 布局 ->单一的
    val simpleItemBinding = itemBindingOf<Int>(me.lx.rv.R.layout.rv_load_more_layout)

    private val mDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            println("加载更多..onChanged().....11111111....")
            mIsLoading = false
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            println("加载更多..onItemRangeRemoved().....44444....${noCanScrollVertically()}")
            mIsLoading = false
            dataChangeNotifyLoadMoreVH()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            println("加载更多..onItemRangeMoved().....555555....")
            mIsLoading = false
            dataChangeNotifyLoadMoreVH()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            println(
                "加载更多  onItemRangeInserted()....6666....positionStart=$positionStart  itemCount=$itemCount  " +
                        "noCanScrollVertically=${noCanScrollVertically()}"
            )
            mIsLoading = false
            dataChangeNotifyLoadMoreVH()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            println("加载更多  onItemRangeChanged().......222222....")
            mIsLoading = false
        }

        // 会执行这
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mIsLoading = false
            println("加载更多 onItemRangeChanged().......3333....${noCanScrollVertically()}")
        }
    }

    private var mRawAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private var mFooter: AbstractLoadMoreFooter

    private var mStateType = STATE_LOADING
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLoadMoreHolder: LoadMoreViewHolder

    /**
     * 是否为上拉
     */
    private var mIsScrollLoadMore = false
    private val mOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            println("onScrollStateChanged.....newState=$newState...mIsScrollLoadMore=$mIsScrollLoadMore")
            if (newState != RecyclerView.SCROLL_STATE_IDLE ||
                mLoadMoreListener == null
            //  || mNoMoreData
            //  || !mIsScrollLoadMore  // 不能用这个变量判断,当loadmore显示的时候,稍微往上移动下dy变成复数,这里就会被拦截
            ) {
                return
            }
            if (canLoadMore(recyclerView.layoutManager)) {
                println("滑动监听......loadMore()....2222222........")
                setState(STATE_LOADING)
                mLoadMoreListener!!.loadingMore()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            mIsScrollLoadMore = dy > 0
        }
    }

    constructor(rawAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, footer: AbstractLoadMoreFooter? = null) : super() {
        mRawAdapter = rawAdapter
        mFooter = footer ?: LoadMoreFooter()
        //if (!isRegistered()) {
        registerAdapterDataObserver(mDataObserver)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
        setFullSpan(recyclerView)
        recyclerView.addOnScrollListener(mOnScrollListener)
        mRawAdapter.onAttachedToRecyclerView(recyclerView)
    }


    override fun getItemCount(): Int {
        val count = mRawAdapter.itemCount
        return if (count > 0) count + 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        //  println("getItemViewType()....isLoadMore=${(position == itemCount - 1 && position != 0)}")
        if (position == itemCount - 1 && position != 0) {
            return VIEW_TYPE_LOAD_MORE;
        }
        return mRawAdapter.getItemViewType(position);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_LOAD_MORE) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.context)
            }
            val loadMoreBinding = DataBindingUtil.inflate(mInflater!!, mLoadMoreRedId, parent, false) as ViewDataBinding
            //加载失败点击事件
            loadMoreBinding.root.setOnClickListener(View.OnClickListener {
                if (mLoadMoreFailClickListener != null && mStateType == STATE_LOAD_FAILED) {
                    mStateType = STATE_LOADING
                    mLoadMoreFailClickListener!!.clickLoadMoreFailView(this@LoadMoreAdapter, loadMoreBinding.root)
                }
            })
            mLoadMoreHolder = LoadMoreViewHolder(loadMoreBinding.root, mFooter)
            return mLoadMoreHolder
        }
        return mRawAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mRawAdapter.onBindViewHolder(holder, position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (holder is LoadMoreViewHolder) {
            //首次如果itemView没有填充满RecyclerView，继续加载更多
            println("onBindViewHolder()....loadMore()...能滑动=${mRecyclerView.canScrollVertically(-1)}")
            if (!mRecyclerView.canScrollVertically(-1) && mLoadMoreListener != null) {
                //fix bug Cannot call this method while RecyclerView is computing a layout or scrolling
                mRecyclerView.post {
                    println("onBindViewHolder()......请求加载更多了....")
                    mStateType = STATE_LOADING
                    mLoadMoreHolder.setState(mStateType)
                    mLoadMoreListener!!.loadingMore()
                }
            }
        } else {
            mRawAdapter.onBindViewHolder(holder, position, payloads)
        }
    }

    private fun dataChangeNotifyLoadMoreVH() {
        if (noCanScrollVertically()) {
            notifyLoadMoreVH()
        }
    }

    private fun noCanScrollVertically(): Boolean {
        return !mRecyclerView.canScrollVertically(-1)
    }

    //    private fun loadingMore() {
//        setState(STATE_LOADING)
//    }
//
//    fun loadFailed() {
//        setState(STATE_LOAD_FAILED)
//    }
//
//    fun noMoreData() {
//        // mNoMoreData = true
//        setState(STATE_NO_MORE_DATA)
//    }
    private fun setState(state: Int) {
        if (mStateType == state) return
        this.mStateType = state
        notifyLoadMoreVH()
    }

    fun setPageSize(pageSize: Int) {
        mPageSize = pageSize
    }

    fun setLoadMoreListener(listener: LoadMoreListener): LoadMoreAdapter {
        this.mLoadMoreListener = listener
        return this
    }


    private fun notifyLoadMoreVH() {
        if (isEnabledOfLoadMore()) {
            notifyItemChanged(itemCount - 1)
        }
    }

    private fun isEnabledOfLoadMore(): Boolean {
        return itemCount > 0
    }

    private fun setFullSpan(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager ?: return

        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val viewType = getItemViewType(position)
                    return if (viewType == VIEW_TYPE_LOAD_MORE) layoutManager.spanCount else 1
                }
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val sgm = layoutManager
        }
    }

    private fun canLoadMore(layoutManager: RecyclerView.LayoutManager?): Boolean {
        var canLoadMore = false
        if (layoutManager is GridLayoutManager) {
            canLoadMore = layoutManager.findLastVisibleItemPosition() >= layoutManager.itemCount - 1
        } else if (layoutManager is LinearLayoutManager) {
            canLoadMore = layoutManager.findLastVisibleItemPosition() >= layoutManager.itemCount - 1
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val into = IntArray(layoutManager.getSpanCount())
            val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(into)
            var lastPosition = lastVisibleItemPositions[0]
            for (value in into) {
                if (value > lastPosition) {
                    lastPosition = value
                }
            }
            canLoadMore = lastPosition >= layoutManager.itemCount - 1
        }
        return canLoadMore
    }

    interface LoadMoreFailClickListener {
        /**
         * 正在加载更多中, 这里需求请求网络
         */
        fun clickLoadMoreFailView(loadMoreAdapter: LoadMoreAdapter, root: View)
    }

    interface LoadMoreListener {
        /**
         * 正在加载更多中, 这里需求请求网络
         */
        fun loadingMore()
    }

    companion object {
        const val VIEW_TYPE_LOAD_MORE = 0
        /**
         * footer的状态
         */
        const val STATE_LOADING = 0
        //    static final int STATE_LOAD_COMPLETE = 1;
        const val STATE_LOAD_FAILED = 2
        const val STATE_NO_MORE_DATA = 3

        fun with(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>): LoadMoreAdapter {
            val loadMoreAdapter = LoadMoreAdapter(adapter)
            return loadMoreAdapter
        }
    }
}