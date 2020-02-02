package me.lx.rv.loadmore

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.lx.rv.BuildConfig
import me.lx.rv.tools.Ls
import me.lx.rv.tools.WrapperUtils


/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/6 09:01
 *  version: 1.0
 *  desc:
 *  注意地方
 *  1、当Item里的数据使用Ob不会更新时,是因为要重写 registerAdapterDataObserver 方法
 *
 *  当有10条数据,输入1 搜索到2条,此时再输入2,没有数据的时候, 就报下面这个错误 todo 2020-1-1
 *    java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionLoadMoreViewHolder{9f83fac position=0 id=-1, oldPos=4, pLpos:4 scrap [attachedScrap] tmpDetached no parent} com.pda.work.base.view.AppRecyclerView{bfad9bc VFED..... ......I.
 *    0,0-1440,1321 #7f080138 app:id/recyclerView}, adapter:me.lx.rv.loadmore.LoadMoreAdapter@b8e2d45
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

    private lateinit var mInnerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var mFooter: AbstractLoadMoreFooter

    private var mLastState = STATE_LOADING
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
                //  Ls.d("监听到底部了.......loadMore()....2222222........mLastState=${mLastState}")
                setState(STATE_LOADING, 1)
                requestLoadingMore(1)
            } else {
                setState(STATE_NO_MORE_DATA, 2)
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
        onConstructor(rawAdapter)
    }

    private fun onConstructor(rawAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        mInnerAdapter = rawAdapter
        mFooter = getLoadMoreFooter()
        mLoadMoreRedId = mFooter.getLayoutRes()
        registerAdapterDataObserver(mDataObserver)
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
        WrapperUtils.onAttachedToRecyclerView(mInnerAdapter, recyclerView, object : WrapperUtils.SpanSizeCallback {
            override fun getWrapSpanSize(
                gridLayoutManager: GridLayoutManager,
                oldLookup: GridLayoutManager.SpanSizeLookup?,
                position: Int
            ): Int {
                if (position < 0) return gridLayoutManager.spanCount
                val viewType = getItemViewType(position)
                if (viewType == VIEW_TYPE_LOAD_MORE) {
                    return gridLayoutManager.spanCount
                } else if (oldLookup != null) {
                    return oldLookup.getSpanSize(position)
                } else {
                    return 1
                }
            }
        })
        recyclerView.addOnScrollListener(mOnScrollListener)
    }


    override fun getItemCount(): Int {
        val count = mInnerAdapter.itemCount
        return if (count > 0) count + 1 else 0
        // return mInnerAdapter.itemCount + if (hasLoadMore()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        //  println("getItemViewType()....isLoadMore=${(position == itemCount - 1 && position != 0)}")
        if (position == itemCount - 1 && position != 0) {
            return VIEW_TYPE_LOAD_MORE;
        }
        return mInnerAdapter.getItemViewType(position);
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
                if (mLastState == STATE_LOAD_FAILED) {
                    setState(STATE_LOADING, 3)
//                    if (mLoadMoreFailClickListener != null) {
//                        mLoadMoreFailClickListener!!.clickLoadMoreFailView(
//                            this@LoadMoreAdapter,
//                            loadMoreBinding.root
//                        )
//                    } else {
//                    }
                     requestLoadingMore(2)
                }
            })
            Ls.d("创建加载更多Holder....onCreateViewHolder()...666....isNull=${mLoadMoreHolder == null} mLastState=$mLastState")
            if (mLoadMoreHolder == null) {
                mLoadMoreHolder = LoadMoreViewHolder(loadMoreBinding.root, mFooter)
                setState(mLastState, 9, true)
            }

            return mLoadMoreHolder!!
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mInnerAdapter.onBindViewHolder(holder, position)
    }

    @CallSuper
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (holder is LoadMoreViewHolder) {
            //首次如果itemView没有填充满RecyclerView，继续加载更多
            // println("onBindViewHolder()....loadMore()...能滑动=${mRecyclerView.canScrollVertically(-1)}")
//            if (!mRecyclerView.canScrollVertically(-1) && mLoadMoreListener != null && canLoadMore(mRecyclerView.layoutManager)) {
//                //fix bug Cannot call this method while RecyclerView is computing a layout or scrolling
//                mRecyclerView.post {
//                    setState(STATE_LOADING, 4)
//                    requestLoadingMore(3)
//                }
//            }
        } else {
            mInnerAdapter.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        mInnerAdapter.onViewAttachedToWindow(holder)
        if (isShowLoadMore(holder.layoutPosition)) {
            WrapperUtils.setFullSpan(holder);
        }
    }
    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.unregisterAdapterDataObserver(observer)
        mInnerAdapter.unregisterAdapterDataObserver(observer)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        mInnerAdapter.onViewDetachedFromWindow(holder)
    }

    override fun getItemId(position: Int): Long {
        return mInnerAdapter.getItemId(position)
    }
//
    override fun setHasStableIds(hasStableIds: Boolean) {
        mInnerAdapter.setHasStableIds(hasStableIds)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return mInnerAdapter.onFailedToRecycleView(holder)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mInnerAdapter.onDetachedFromRecyclerView(recyclerView)
    }
//
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        mInnerAdapter.onViewRecycled(holder)
    }

    /**
     * 一定要重写该方法
     */
    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        mInnerAdapter.registerAdapterDataObserver(observer)
    }

    private fun isShowLoadMore(position: Int): Boolean {
        return position >= mInnerAdapter.itemCount && mInnerAdapter.itemCount > 0
    }

    private fun dataChangeNotifyLoadMoreVH() {
//        if (noCanScrollVertically()) {
//            notifyLoadMoreVH()
//        }
    }

    private fun noCanScrollVertically(): Boolean {
        return !mRecyclerView.canScrollVertically(-1)
    }

    private fun requestLoadingMore(flag: Int) {
        val enabledOfLoadMore = isEnabledOfLoadMore()
        Ls.d("requestLoadingMore()...触发了66....flag=$flag  itemCount=$itemCount enabledOfLoadMore=$enabledOfLoadMore")
        if (enabledOfLoadMore) {
            mLoadMoreListener!!.isShowLoadMoreFailOb()?.set(false)
            mLoadMoreListener!!.onLoadingMore()
        }
    }

    private fun getStateText(state: Int): String {
        return when (state) {
            STATE_LOADING -> {
                "加载中"
            }
            STATE_NO_MORE_DATA -> {
                "无更多"
            }
            STATE_LOAD_FAILED -> {
                "失败"
            }
            STATE_HIDE_LAYOUT -> {
                "隐藏"
            }
            else -> {
                "未知"
            }
        }
    }

    private fun setState(state: Int, flag: Int, isForceSet: Boolean = false) {

        if (DEBUG) {
            val isShowViewOb = mLoadMoreListener!!.isShowLoadMoreViewLayout()
            val isShowView = isShowViewOb == null || isShowViewOb.get()
            Ls.d(
                "设置加载更多状态...setState()..222...newState=${getStateText(state)} " +
                        " mLastState=${getStateText(mLastState)}  flag=$flag isForceSet=$isForceSet  showView=${isShowView}"
            )
        }
        if (mLastState == state && !isForceSet) return

        //if (isShowView) {
        mLastState = state
        if (mLoadMoreHolder != null) {
            // 延迟更新UI的原因是, 当加载后,添加item,刷新列表时,会看到无更多数据的脚布局,体验不好
            mRecyclerView.postDelayed({
                mLoadMoreHolder!!.setState(state)
                notifyLoadMoreVH()
            }, 500)
        }
        // }
    }


    fun listenerNoMoreDataAndFail(listener: LoadMoreListener): LoadMoreAdapter {
        this.mLoadMoreListener = listener
        listener.isShowNoMoreDataOb()
            ?.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                    mNoMoreData = (sender as ObservableBoolean).get()
                    Ls.d("加载更多..没有更多数据..666..onPropertyChanged..value=${mNoMoreData}")
                    if (mNoMoreData) {
                        setState(STATE_NO_MORE_DATA, 5)
                    } else {
                        setState(STATE_LOADING, 6)
                    }
                }
            })
        listener.isShowLoadMoreFailOb()
            ?.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                    val isShowFail = (sender as ObservableBoolean).get()
                    if (isShowFail) {
                        setState(STATE_LOAD_FAILED, 7)
                    } else {
                        setState(STATE_LOADING, 8)
                    }
                }
            })
        if (listener.isShowLoadMoreViewLayout() != null) {
            listener.isShowLoadMoreViewLayout()!!
                .addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                        val isShowView = (sender as ObservableBoolean).get()
                        if (isShowView) {
                            setState(mLastState, 10)
                        } else {
                            setState(STATE_HIDE_LAYOUT, 11)
                        }
                    }
                })
            listener.isShowLoadMoreViewLayout()!!.notifyChange()
        }

        return this
    }


    private fun notifyLoadMoreVH() {
        if (isEnabledOfLoadMore()) {
            notifyItemChanged(itemCount - 1)
        }
    }

    private fun isEnabledOfLoadMore(): Boolean {
        return mLoadMoreHolder != null && itemCount > 0
    }

    private fun setFullSpan(recyclerView: RecyclerView) {

    }

    private fun canLoadMore(layoutManager: RecyclerView.LayoutManager?): Boolean {
        if (mLoadMoreListener?.isShowNoMoreDataOb()?.get() == true) {
            return false
        }
        var canLoadMore = false
        if (layoutManager is GridLayoutManager) {
            canLoadMore = layoutManager.findLastVisibleItemPosition() >= layoutManager.itemCount - 1
        } else if (layoutManager is LinearLayoutManager) {
            canLoadMore = layoutManager.findLastVisibleItemPosition() >= layoutManager.itemCount - 1
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val into = IntArray(layoutManager.spanCount)
            val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(into)
            var lastPosition = lastVisibleItemPositions[0]
            for (value in into) {
                if (value > lastPosition) {
                    lastPosition = value
                }
            }
            canLoadMore = lastPosition > layoutManager.itemCount
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
         * 显示没有更多数据的标识
         */
        fun isShowNoMoreDataOb(): ObservableBoolean? {
            return null
        }

        /**
         * 显示加载更多失败的标识
         */
        fun isShowLoadMoreFailOb(): ObservableBoolean? {
            return null
        }

        // 是否显示加载更多的布局
        fun isShowLoadMoreViewLayout(): ObservableBoolean? {
            return null
        }
    }

    companion object {
        const val VIEW_TYPE_LOAD_MORE = 0 // 加载更多类型

        private val DEBUG = BuildConfig.DEBUG

        @JvmField
        var DEFAULT_FOOTER_PATH: String? = null
        /**
         * footer的状态
         */
        const val STATE_LOADING = 1
        const val STATE_NO_MORE_DATA = 2
        const val STATE_LOAD_FAILED = 3
        const val STATE_HIDE_LAYOUT = 4

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