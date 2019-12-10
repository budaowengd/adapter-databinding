package me.lx.rv.loadmore

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/6 09:01
 *  version: 1.0
 *  desc:
 */
open class LoadMoreAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private var mInflater: LayoutInflater? = null
    @LayoutRes
    protected var mLoadMoreRedId = 0


    private var mIsLoading: Boolean = false

    private var mLoadMoreListener: LoadMoreListener? = null
    private var mLoadMoreFailClickListener: LoadMoreFailClickListener? = null

    // 布局 ->单一的
    // val simpleItemBinding = itemBindingOf<Int>(me.lx.rv.R.layout.rv_load_more_layout)

    private val mDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            //  println("加载更多..onChanged().....11111111....")
            mIsLoading = false
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            //  println("加载更多..onItemRangeRemoved().....44444....${noCanScrollVertically()}")
            mIsLoading = false
            dataChangeNotifyLoadMoreVH()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            // println("加载更多..onItemRangeMoved().....555555....")
            mIsLoading = false
            dataChangeNotifyLoadMoreVH()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mIsLoading = false
            dataChangeNotifyLoadMoreVH()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            //println("加载更多  onItemRangeChanged().......222222....")
            mIsLoading = false
        }

        // 会执行这
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mIsLoading = false
            // println("加载更多 onItemRangeChanged().......3333....positionStart=${positionStart}..itemCount=${itemCount}")
        }
    }

    private var mRawAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private var mFooter: AbstractLoadMoreFooter

    private var mCurrentState = STATE_LOADING
    private lateinit var mRecyclerView: RecyclerView
    private var mLoadMoreHolder: LoadMoreViewHolder? = null
    private var mNoMoreData = false // 没有更多数据的标识
    private var mIfNoMoreDataHideLayout = false // 如果没有更多数据,是否隐藏底部布局
    /**
     * 是否为上拉
     */
    private var mIsScrollLoadMore = false
    private val mOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            // println("onScrollStateChanged.....newState=$newState...mNoMoreData=$mNoMoreData")
            if (newState != RecyclerView.SCROLL_STATE_IDLE ||
                    mLoadMoreListener == null
                    || mNoMoreData
            //  || !mIsScrollLoadMore  // 不能用这个变量判断,当loadmore显示的时候,稍微往上移动下dy变成复数,这里就会被拦截
            ) {
                return
            }
            if (canLoadMore(recyclerView.layoutManager)) {
                //  println("监听到底部了.......loadMore()....2222222........mCurrentState=${mCurrentState}")
                setState(STATE_LOADING)
                requestLoadingMore()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            mIsScrollLoadMore = dy > 0
        }
    }

    constructor(
            rawAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
            //,footer: AbstractLoadMoreFooter? = null
    ) : super() {
        mRawAdapter = rawAdapter
        mFooter = getLoadMoreFooter()
        mLoadMoreRedId = mFooter.getLayoutRes()
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
            // println("onCreateViewHolder()....创建Holder....不为空=${mLoadMoreHolder != null}")
            if (mLoadMoreHolder != null) return mLoadMoreHolder!!

            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.context)
            }
            val loadMoreBinding = DataBindingUtil.inflate(
                    mInflater!!,
                    mLoadMoreRedId,
                    parent,
                    false
            ) as ViewDataBinding
            //加载失败点击事件
            loadMoreBinding.root.setOnClickListener(View.OnClickListener {
                if (mCurrentState == STATE_LOAD_FAILED) {
                    setState(STATE_LOADING)
                    if (mLoadMoreFailClickListener != null) {
                        mLoadMoreFailClickListener!!.clickLoadMoreFailView(
                                this@LoadMoreAdapter,
                                loadMoreBinding.root
                        )
                    } else {
                        requestLoadingMore()
                    }
                }
            })

            if (mLoadMoreHolder == null) {
                mLoadMoreHolder = LoadMoreViewHolder(loadMoreBinding.root, mFooter)
            }

            return mLoadMoreHolder!!
        }
        return mRawAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mRawAdapter.onBindViewHolder(holder, position)
    }

    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: MutableList<Any>
    ) {
        if (holder is LoadMoreViewHolder) {
            //首次如果itemView没有填充满RecyclerView，继续加载更多
            // println("onBindViewHolder()....loadMore()...能滑动=${mRecyclerView.canScrollVertically(-1)}")
            if (!mRecyclerView.canScrollVertically(-1) && mLoadMoreListener != null) {
                //fix bug Cannot call this method while RecyclerView is computing a layout or scrolling
                mRecyclerView.post {
                    setState(STATE_LOADING)
                    requestLoadingMore()
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


    private fun requestLoadingMore() {
        mLoadMoreListener!!.isShowLoadMoreFailOb()?.set(false)
        mLoadMoreListener!!.onLoadingMore()
    }


    private fun setState(state: Int) {
        if (mCurrentState == state) return
        mCurrentState = state
        // 延迟更新UI的原因是, 当加载后,添加item,刷新列表时,会看到无更多数据的脚布局,体验不好
        if(mLoadMoreHolder!=null){
            mRecyclerView.postDelayed({
                mLoadMoreHolder!!.setState(state)
                notifyLoadMoreVH()
            }, 600)
        }
    }


    fun listenerNoMoreDataAndFail(listener: LoadMoreListener): LoadMoreAdapter {
        this.mLoadMoreListener = listener
        listener.isShowNoMoreDataOb()
                ?.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                        mNoMoreData = (sender as ObservableBoolean).get()
                        if (mNoMoreData) {
                            setState(STATE_NO_MORE_DATA)
                        } else {
                            setState(STATE_LOADING)
                        }
                    }
                })
        listener.isShowLoadMoreFailOb()
                ?.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                        val isShowFail = (sender as ObservableBoolean).get()
                        if (isShowFail) {
                            setState(STATE_LOAD_FAILED)
                        } else {
                            setState(STATE_LOADING)
                        }
                    }
                })
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
            // val sgm = layoutManager
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

    /**
     * 如果没有更多的数据后,隐藏底部没有更多数据的提示
     */
    fun setIfNoMoreDataHideLayout() {
        mIfNoMoreDataHideLayout = true
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
        fun onLoadingMore()

        /**
         * 获取没有更多数据的标识
         */
        fun isShowNoMoreDataOb(): ObservableBoolean? {
            return null
        }

        /**
         * 是否加载更多失败的标识
         */
        fun isShowLoadMoreFailOb(): ObservableBoolean? {
            return null
        }
    }

    companion object {
        const val VIEW_TYPE_LOAD_MORE = 0 // 加载更多类型

        @JvmField
        var DEFAULT_FOOTER_PATH: String? = null
        /**
         * footer的状态
         */
        const val STATE_LOADING = 1
        const val STATE_LOAD_FAILED = 2
        const val STATE_NO_MORE_DATA = 3

        fun with(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>): LoadMoreAdapter {
            val loadMoreAdapter = LoadMoreAdapter(adapter)
            return loadMoreAdapter
        }

        fun getLoadMoreFooter(): AbstractLoadMoreFooter {
            var footer: AbstractLoadMoreFooter? = null
            if (!TextUtils.isEmpty(DEFAULT_FOOTER_PATH)) {
                val footerClass = Class.forName(DEFAULT_FOOTER_PATH!!)
                footer = footerClass.newInstance() as AbstractLoadMoreFooter
            }
            if (footer == null) {
                footer = LoadMoreFooter()
            }
            return footer
        }
    }
}