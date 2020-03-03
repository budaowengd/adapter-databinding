package me.lx.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.OnRebindCallback
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * 参考： https://github.com/evant/binding-collection-adapter
  * [RecyclerView.Adapter]使用给定的[XmlItemBinding]将项目绑定到布局。
  * 如果你数据源是[ObservableList]，会根据对更改自行更新
 */
class BindingRecyclerViewAdapter<T> : RecyclerView.Adapter<ViewHolder>(), BindingCollectionAdapter<T> {
    /**
     * 添加或者删除item元素时,是否移动到指定的位置
     */
    private var addOrRemoveSmoothSpecPosition: Boolean? = null
    private lateinit var xmlItemBinding: XmlItemBinding<T>
    private var callback: WeakReferenceOnListChangedCallback<T>? = null
    private var items: List<T>? = null
    private var inflater: LayoutInflater? = null
    private var itemIds: ItemIds<in T>? = null
    private var viewHolderFactory: ViewHolderFactory? = null
    private var mRecyclerView: RecyclerView? = null
    private var lifecycleOwner: LifecycleOwner? = null
    private var mDecorator: OnBindViewHolderDecorator<T>? = null

    /**
     * Fragment的model里有个Adapter的时候,图片加载的默认会使用itemView的context,
     * 当改变语言后,Act重建了,但是Adapter还是同1个,因为model不会重建,Adapter在model里,
     * 所以inflater的对象是通过之前Act创建的,之前Act因为已经被销毁了,所以通过itemView的上下文创建图片的时候
     * 会出现 You cannot start a load for a destroyed activity
     * 解决方案:
     * 1、Adapter重新被附在Rv的时候,把 inflater 置为空
     * 2、设置Adapter为空,会回调 onDetachedFromRecyclerView ,和 unRisterObserver()方法
     */
    fun init() {
        inflater = null
        lifecycleOwner = null
        // && mRecyclerView!!.adapter == this 不能添加这个判断,因为adapter有可能是loadmoreAdapter
        if (mRecyclerView != null) {
            mRecyclerView!!.adapter = null // 会回调 onDetachedFromRecyclerView 方法
            callback=null
            mRecyclerView = null
//            onDetachedFromRecyclerView(mRecyclerView!!)
//        this.unregisterAdapterDataObserver(observ)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        //  Ls.d("onAttachedToRecyclerView()....1111...rv=${recyclerView.hashCode()}  if=${mRecyclerView == null && items is ObservableList<T>}")
        if (mRecyclerView == null && items is ObservableList<T>) {
            callback = WeakReferenceOnListChangedCallback<T>(recyclerView, this, items as ObservableList<T>)
            (items as ObservableList<T>).addOnListChangedCallback(callback)
        }
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, layoutId: Int): ViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(viewGroup.context)
        }
        val binding = onCreateBinding(inflater!!, layoutId, viewGroup)
        val holder = onCreateViewHolder(binding)
        binding.addOnRebindCallback(object : OnRebindCallback<ViewDataBinding>() {
            override fun onPreBind(binding: ViewDataBinding?): Boolean {
                return mRecyclerView != null && mRecyclerView!!.isComputingLayout
            }

            override fun onCanceled(binding: ViewDataBinding?) {
                if (mRecyclerView == null || mRecyclerView!!.isComputingLayout) {
                    return
                }
                val position = holder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    try {
                        getRvAdapter().notifyItemChanged(position, DATA_INVALIDATION)
                    } catch (e: IllegalStateException) {
                        // noop - this shouldn't be happening
                    }
                }
            }
        })
        return holder
    }

    override fun onCreateBinding(inflater: LayoutInflater, @LayoutRes layoutRes: Int, viewGroup: ViewGroup): ViewDataBinding {
        return DataBindingUtil.inflate(inflater, layoutRes, viewGroup, false)
    }

    override fun onBindBinding(binding: ViewDataBinding, variableId: Int, @LayoutRes layoutRes: Int, position: Int, item: T) {
        tryGetLifecycleOwner()
//        if (DEBUG) {
//            Ls.d("onBindBinding()....position=$position   mExtraBindings")
//        }
        xmlItemBinding.bind(binding, item)
        if (lifecycleOwner != null) {
            binding.lifecycleOwner = lifecycleOwner
        }
    }


    /**
     * 只有在Rv重复调用setAdapter()方法才会被调用,所以我们在init里调用,防止内存泄漏
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        if (mRecyclerView != null && items is ObservableList<T>) {
            (items as ObservableList<T>).removeOnListChangedCallback(callback)
            callback = null
        }
        mRecyclerView = null

    }


    override fun getItemViewType(position: Int): Int {
        xmlItemBinding.onGetItemViewType(position, items!![position])
        return xmlItemBinding.getLayoutRes()
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // This won't be called by recyclerview since we are overriding the other overload, call
        // the other overload here in case someone is calling this directly ex: in a test.
        onBindViewHolder(viewHolder, position, emptyList())
    }

    @CallSuper
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        val binding = DataBindingUtil.getBinding<ViewDataBinding>(holder.itemView)!!
//        if (isForDataBinding(payloads)) { // 局部更新
        // 完整更新
        val item = items!![position]
        onBindBinding(binding, xmlItemBinding.getDefaultVariableId(), xmlItemBinding.getLayoutRes(), position, item)
        // 对外开放接口
        if (mDecorator != null) {
            mDecorator!!.decorator(item, position, binding)
        }
        binding.executePendingBindings()
    }

    fun setBindViewHolderDecorator(decorator: OnBindViewHolderDecorator<T>) {
        mDecorator = decorator
    }

    private fun isForDataBinding(payloads: List<Any>): Boolean {
        if (payloads.isEmpty()) {
            return false
        }
        for (i in payloads.indices) {
            val obj = payloads[i]
            if (obj !== DATA_INVALIDATION) {
                return false
            }
        }
        return true
    }

    /**
     * Constructs a view holder for the given databinding. The default implementation is to use
     * [ViewHolderFactory] if provided, otherwise use a default view holder.
     */
    fun onCreateViewHolder(binding: ViewDataBinding): ViewHolder {
        return if (viewHolderFactory != null) {
            viewHolderFactory!!.createViewHolder(binding)
        } else {
            BindingViewHolder(binding)
        }
    }

    override fun setItemBinding(itemBinding: XmlItemBinding<T>) {
        this.xmlItemBinding = itemBinding
    }

    /**
     * Sets the lifecycle owner of this adapter to work with [androidx.lifecycle.LiveData].
     * This is normally not necessary, but due to an androidx limitation, you need to set this if
     * the containing view is *not* using databinding.
     */
    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner?) {
        this.lifecycleOwner = lifecycleOwner
        if (mRecyclerView != null) {
            for (i in 0 until mRecyclerView!!.childCount) {
                val child = mRecyclerView!!.getChildAt(i)
                val binding = DataBindingUtil.getBinding<ViewDataBinding>(child)
                if (binding != null) {
                    binding.lifecycleOwner = lifecycleOwner
                }
            }
        }
    }

    override fun getItemXmlObj(): XmlItemBinding<T> {
        return xmlItemBinding
    }


    override fun setItems(items: List<T>) {
        //  Ls.d("setItems()...2222222..if=${items == this.items}")
        if (items == this.items) {
            return
        }
        // If a recyclerview is listening, set up listeners. Otherwise wait until one is attached.
        // No need to make a sound if nobody is listening right?
        if (mRecyclerView != null) {
            if (this.items is ObservableList<T>) {
                (this.items as ObservableList<T>).removeOnListChangedCallback(callback)
                callback = null
            }
            if (items is ObservableList<T>) {
                callback = WeakReferenceOnListChangedCallback(mRecyclerView!!, this, items)
                items.addOnListChangedCallback(callback)
            }
        }
        this.items = items
        getRvAdapter().notifyDataSetChanged()
    }

    private fun getRvAdapter(): RecyclerView.Adapter<*> {

        return mRecyclerView?.adapter ?: this
    }

    override fun getAdapterItem(position: Int): T {
        return items!![position]
    }


    /**
     * IllegalStateException -> Cannot change whether this adapter has stable IDs while the adapter has registered observers
     * Set the item id's for the items. If not null, this will set [ ][RecyclerView.Adapter.setHasStableIds] to true.
     */
    fun setItemIds(ids: ItemIds<in T>?) {
        if (itemIds == null && itemIds != ids) {
            this.itemIds = ids
            setHasStableIds(true)
        }
    }

    /**
     * 此方法只在setHasStableIds设置为true才会生效
     * 1、如果只返回position,当item位置改变,数据会错乱
     * 2、不重写的话,grid=2的布局时刷新会闪烁,因为item重新测量,要么写死item的宽高解决问题
     * 3、要么返回具体的itemId.如果返回postion, 列表滑动到下面,此时刷新还是会闪烁
     */
    override fun getItemId(position: Int): Long {
//        Ls.d("getItemId()...111...position=$position")
//        return position.toLong()
//        // super.getItemId(position)
        return if (itemIds == null) position.toLong() else itemIds!!.getItemId(position, items!![position])
    }

    /**
     * Set the factory for creating view holders. If null, a default view holder will be used. This
     * is useful for holding custom state in the view holder or other more complex customization.
     */
    fun setViewHolderFactory(factory: ViewHolderFactory?) {
        viewHolderFactory = factory
    }


    private fun tryGetLifecycleOwner() {
        if (lifecycleOwner == null || lifecycleOwner!!.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            lifecycleOwner = Utils.findLifecycleOwner(mRecyclerView)
        }
    }

    fun smoothSpecPosition(recyclerView: RecyclerView, positionStart: Int) {
        if (addOrRemoveSmoothSpecPosition == true) {
            recyclerView.scrollToPosition(positionStart)
        }
    }

    fun setAddOrRemoveSmoothSpecPosition(isSmoothSpecPosition: Boolean = true) {
        addOrRemoveSmoothSpecPosition = isSmoothSpecPosition
    }


    class WeakReferenceOnListChangedCallback<T> constructor(
        var recyclerView: RecyclerView, var rvAdapter: BindingRecyclerViewAdapter<T>, items:
        ObservableList<T>
    ) : ObservableList.OnListChangedCallback<ObservableList<T>>() {
        //         internal val adapterRef: WeakReference<RecyclerView.Adapter<ViewHolder>> = AdapterReferenceCollector.createRef(adapter, items, this)
        override fun onChanged(sender: ObservableList<T>) {
            // println("BindingRecyclerViewAdapter()....onChanged()...2222.")
            Utils.ensureChangeOnMainThread()
            val adapter = recyclerView.adapter ?: return
            adapter.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
            Utils.ensureChangeOnMainThread()
            rvAdapter.getRvAdapter().notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
            Utils.ensureChangeOnMainThread()
//            if (DEBUG) {
//                Log.i(TAG, "onItemRangeInserted()..111..positionStart=$positionStart  itemCount=$itemCount")
//            }
            rvAdapter.smoothSpecPosition(recyclerView, positionStart)
            rvAdapter.getRvAdapter().notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeMoved(sender: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
            Utils.ensureChangeOnMainThread()
//            if (DEBUG) {
//                Log.i(TAG, "onItemRangeMoved()...222...fromPosition=$fromPosition  toPosition=$toPosition" + "  itemCount=$itemCount")
//            }
            for (i in 0 until itemCount) {
                rvAdapter.getRvAdapter().notifyItemMoved(fromPosition + i, toPosition + i)
            }
        }

        override fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
            Utils.ensureChangeOnMainThread()
//            if (DEBUG) {
//                Log.i(TAG, "onItemRangeMoved()...333...positionStart=$positionStart    itemCount=$itemCount")
//            }
            rvAdapter.smoothSpecPosition(recyclerView, positionStart)
            // val adapter = adapterRef.get() ?: return
            rvAdapter.getRvAdapter().notifyItemRangeRemoved(positionStart, itemCount)
        }
    }

    private class BindingViewHolder internal constructor(binding: ViewDataBinding) : ViewHolder(binding.root)


    interface ItemIds<T> {
        fun getItemId(position: Int, item: T): Long
    }

    interface ViewHolderFactory {
        fun createViewHolder(binding: ViewDataBinding): ViewHolder
    }

    companion object {
        private val DATA_INVALIDATION = Any()

        val TAG = "me_lx_rv"
        val DEBUG = BuildConfig.DEBUG
    }
}
