package me.lx.rv.loadmore

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.*
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
 *  1、RecyclerView 不应该被当前类持有
 *  2、mLoadMoreListener 是在model里的,会被一直持有,所以里面的每个ob对象都得重新设置监听,设置过的得移除
 *  1、当Item里的数据使用Ob不会更新时,是因为要重写 registerAdapterDataObserver 方法
 *
 *  当有10条数据,输入1 搜索到2条,此时再输入2,没有数据的时候, 就报下面这个错误 todo 2020-1-1
 *    java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionLoadMoreViewHolder{9f83fac position=0 id=-1, oldPos=4, pLpos:4 scrap [attachedScrap] tmpDetached no parent} com.pda.work.base.view.AppRecyclerView{bfad9bc VFED..... ......I.
 *    0,0-1440,1321 #7f080138 app:id/recyclerView}, adapter:me.lx.rv.loadmore.LoadMoreAdapter@b8e2d45
 */
@Suppress("UNCHECKED_CAST")
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
            Ls.d("加载更多..onChanged().....11111111....")
            mIsLoading = false
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            Ls.d("加载更多..onItemRangeRemoved()..44444..noCanScroll=${noCanScrollVertically()} positionStart=$positionStart itemCount=$itemCount getCount=${mInnerAdapter.itemCount})")
            mIsLoading = false
            dataChangeNotifyLoadMoreVH()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            Ls.d("加载更多..onItemRangeMoved()..55555..fromPosition=$fromPosition toPosition=$toPosition itemCount=$itemCount getCount=${mInnerAdapter.itemCount}")
            mIsLoading = false
            dataChangeNotifyLoadMoreVH()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            Ls.d("加载更多  onItemRangeInserted().......666666....")
            mIsLoading = false
            dataChangeNotifyLoadMoreVH()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            Ls.d("加载更多  onItemRangeChanged().......777777....")
            mIsLoading = false
        }

        // 会执行这
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mIsLoading = false
            Ls.d("加载更多 onItemRangeChanged().......3333....positionStart=${positionStart}..itemCount=${itemCount}")
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
            Ls.d("onScrollStateChanged.....newState=$newState...mNoMoreData=$mNoMoreData")
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
        // registerAdapterDataObserver(mDataObserver)
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        Ls.d("loadMore...onAttachedToRecyclerView()..111...rv=${recyclerView.hashCode()} this=${hashCode()}")
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
        if (position == itemCount - 1 && position != 0) {
            Ls.d("getItemViewType()....isLoadMore  itemCount=${(itemCount)}")
            return VIEW_TYPE_LOAD_MORE;
        }
        return mInnerAdapter.getItemViewType(position);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_LOAD_MORE) {
            Ls.d("onCreateViewHolder()....创建LoadMoreHolder前....loadmoreIsNull=${mLoadMoreHolder == null}")
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
            Ls.d("创建加载更多Holder....onCreateViewHolder()...666....isNull=${mLoadMoreHolder == null} mLastState=${getStateText(mLastState)}")
            if (mLoadMoreHolder == null) {
                mLoadMoreHolder = LoadMoreViewHolder(loadMoreBinding.root, mFooter)
                // 这里必须得更新下,当第一次加载到3条就没有更多数据了,得更新文案
                // true也是必须的,第一次创建一定要设置下数据
                setState(mLastState, 9, true) // todo 这里第3个参数是true,不需要设置吧,默认就是正常状态
            }

            return mLoadMoreHolder!!
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mInnerAdapter.onBindViewHolder(holder, position)
    }

    /*
    * 1、当只有2条数据,没有铺满屏幕,执行onBindViewHolder()方法的话,
    *   先判断屏幕能否滑动,不能的话,请求加载更多,.. 只有2条数据,一般是不能滑动的
    * 2、用index记录,第1次=1,才去判断是否滑动
    * */
    private var index = 0

    @CallSuper
    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: List<Any>
    ) {
        if (holder is LoadMoreViewHolder) {
            if (index == 0) {
                index = 1
                if (!mRecyclerView.canScrollVertically(-1)) {
                    // 如果不能滑动,强制请求加载更多
                    requestLoadingMore(3)
                }
            }
            //首次如果itemView没有填充满RecyclerView，继续加载更多
            Ls.d("onBindViewHolder()....loadMore()...能滑动=${mRecyclerView.canScrollVertically(-1)}")
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
        //当使用ViewPager绑定View的时候会报错: IllegalStateException("Observer " + observer + " was not registered
        //  mInnerAdapter.unregisterAdapterDataObserver(observer)
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
        super.registerAdapterDataObserver(mDataObserver)
        // mInnerAdapter.registerAdapterDataObserver(observer)
//        Ls.d("registerAdapterDataObserver()...2222.....code=${hashCode()}")
    }

    private fun isShowLoadMore(position: Int): Boolean {
        return position >= mInnerAdapter.itemCount && mInnerAdapter.itemCount > 0
    }

    private fun dataChangeNotifyLoadMoreVH() {
        if (itemCount == 0) {
            notifyDataSetChanged()
        }
//        if (noCanScrollVertically()) {
//            notifyLoadMoreVH()
//        }
    }

    private fun noCanScrollVertically(): Boolean {
        return !mRecyclerView.canScrollVertically(-1)
    }

    private fun requestLoadingMore(flag: Int) {
        val enabledOfLoadMore = isEnabledOfLoadMore()
        Ls.d("requestLoadingMore()...触发了66....flag=$flag  itemCount=$itemCount enabled=$enabledOfLoadMore mNoMoreData=$mNoMoreData")
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
                            " mLastState=${getStateText(mLastState)}  " +
                            "flag=$flag isForceSet=$isForceSet  showView=${isShowView} mNoMoreData=$mNoMoreData"
            )
        }
        if (mLastState == state && !isForceSet) return

        //if (isShowView) {
        mLastState = state
        if (mLoadMoreHolder != null) {
            // 延迟更新UI的原因是, 当加载后,添加item,刷新列表时,会看到无更多数据的脚布局,体验不好
            mLoadMoreHolder!!.setState(state)
//            mRecyclerView.postDelayed({
//                notifyLoadMoreVH()
//            }, 500)
        }
        // }
    }

    /**
     * 不清除回调会导致内存泄漏
     * 比如: LoadMoreListener是在model里的,当ViewPager有多个frag,其中第1个frag销毁了,再重新创建的时候,
     * ob还是会有之前的引用
     */
    private fun getBaseObservableClass(ob: BaseObservable): Class<BaseObservable> {
        val superclass = ob.javaClass.superclass?.superclass
        if (superclass == BaseObservable::class.java) {
            return superclass
        }
        return superclass?.superclass as Class<BaseObservable>
    }

    private fun clearObCallback(ob: BaseObservable?) {
        ob ?: return
//        Ls.d("clearObCallback()..ob=${ob.javaClass}  s1=${ob.javaClass.superclass} s2=${ob.javaClass.superclass?.superclass}")
        // 暴力反射获取属性
        val field = getBaseObservableClass(ob).getDeclaredField("mCallbacks")
        // 设置反射时取消Java的访问检查，暴力访问
        field.isAccessible = true
        val callbackObj = field.get(ob)
        if (callbackObj is PropertyChangeRegistry) {
            callbackObj.clear()
        }
    }

    fun listenerNoMoreDataAndFail(listener: LoadMoreListener): LoadMoreAdapter {
        this.mLoadMoreListener = listener
        // 防止内存泄漏.因为listener属于model,是长生命周期,当前对象是短生命的,走这个方法,得把之前对象设置的引用监听得全部移除掉
        clearObCallback(listener.isShowLoadMoreFailOb())
        clearObCallback(listener.isShowNoMoreDataOb())
        clearObCallback(listener.isShowLoadMoreViewLayout())
        val noMoreDataOb = listener.isShowNoMoreDataOb()
        if (noMoreDataOb != null) {
            mNoMoreData = noMoreDataOb.get() // 这里要赋值
            noMoreDataOb.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
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
        }
        if (mNoMoreData) {
            mLastState = STATE_NO_MORE_DATA
        }
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
        val showLoadMore = listener.isShowLoadMoreViewLayout()
        if (showLoadMore != null) {
            showLoadMore
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
            showLoadMore.notifyChange()
        }

        return this
    }


    private fun notifyLoadMoreVH() {
        if (isEnabledOfLoadMore()) {
            Ls.d("noti@fyLoadMoreVH()...66666....")
            notifyItemChanged(itemCount - 1)
        }
    }

    private fun isEnabledOfLoadMore(): Boolean {
        return mLoadMoreHolder != null && itemCount > 0 && !mNoMoreData
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