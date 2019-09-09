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
import java.lang.ref.WeakReference

/**
 * A [RecyclerView.Adapter] that binds items to layouts using the given [XmlItemBinding].
 * If you give it an [ObservableList] it will also updated itself based on changes to that
 * list.
 */
class BindingRecyclerViewAdapter<T> : RecyclerView.Adapter<ViewHolder>(),BindingCollectionAdapter<T> {

    private lateinit var xmlItemBinding: XmlItemBinding<T>
    private var callback: WeakReferenceOnListChangedCallback<T>? = null
    private var items: List<T>? = null
    private var inflater: LayoutInflater? = null
    private var itemIds: ItemIds<in T>? = null
    private var viewHolderFactory: ViewHolderFactory? = null
    private var recyclerView: RecyclerView? = null
    private var lifecycleOwner: LifecycleOwner? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        if (this.recyclerView == null && items is ObservableList<T>) {
            callback = WeakReferenceOnListChangedCallback<T>(this, items as ObservableList<T>)
            (items as ObservableList<T>).addOnListChangedCallback(callback)
        }
        this.recyclerView = recyclerView
    }

    override fun onCreateBinding(
        inflater: LayoutInflater, @LayoutRes layoutRes: Int,
        viewGroup: ViewGroup
    ): ViewDataBinding {
        return DataBindingUtil.inflate(inflater, layoutRes, viewGroup, false)
    }

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int, @LayoutRes layoutRes: Int,
        position: Int,
        item: T
    ) {
        tryGetLifecycleOwner()
        if (xmlItemBinding.bind(binding, item)) {
            binding.executePendingBindings()
            if (lifecycleOwner != null) {
                binding.lifecycleOwner = lifecycleOwner
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        if (this.recyclerView != null && items is ObservableList<T>) {
            (items as ObservableList<T>).removeOnListChangedCallback(callback)
            callback = null
        }
        this.recyclerView = null
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, layoutId: Int): ViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(viewGroup.context)
        }
        val binding = onCreateBinding(inflater!!, layoutId, viewGroup)
        val holder = onCreateViewHolder(binding)
        binding.addOnRebindCallback(object : OnRebindCallback<ViewDataBinding>() {
            override fun onPreBind(binding: ViewDataBinding?): Boolean {
                return recyclerView != null && recyclerView!!.isComputingLayout
            }

            override fun onCanceled(binding: ViewDataBinding?) {
                if (recyclerView == null || recyclerView!!.isComputingLayout) {
                    return
                }
                val position = holder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    try {
                        notifyItemChanged(position, DATA_INVALIDATION)
                    } catch (e: IllegalStateException) {
                        // noop - this shouldn't be happening
                    }
                }
            }
        })
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        xmlItemBinding.xmlOnItemBind(position, items!![position])
        return xmlItemBinding.getLayoutRes()
    }

    override fun getItemCount(): Int {
        return items!!.size
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
        if (recyclerView != null) {
            for (i in 0 until recyclerView!!.childCount) {
                val child = recyclerView!!.getChildAt(i)
                val binding = DataBindingUtil.getBinding<ViewDataBinding>(child)
                if (binding != null) {
                    binding.lifecycleOwner = lifecycleOwner
                }
            }
        }
    }

    override fun getItemBinding(): XmlItemBinding<T> {
        return xmlItemBinding
    }

    override fun setItems(items: List<T>) {
        if (items == this.items) {
            return
        }
        // If a recyclerview is listening, set up listeners. Otherwise wait until one is attached.
        // No need to make a sound if nobody is listening right?
        if (recyclerView != null) {
            if (this.items is ObservableList<T>) {
                (this.items as ObservableList<T>).removeOnListChangedCallback(callback)
                callback = null
            }
            if (items is ObservableList<T>) {
                callback = WeakReferenceOnListChangedCallback(this, items)
                items.addOnListChangedCallback(callback)
            }
        }
        this.items = items
        notifyDataSetChanged()
    }

    override fun getAdapterItem(position: Int): T {
        return items!![position]
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


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // This won't be called by recyclerview since we are overriding the other overload, call
        // the other overload here in case someone is calling this directly ex: in a test.

        onBindViewHolder(viewHolder, position, emptyList())

    }

    @CallSuper
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        val binding = DataBindingUtil.getBinding<ViewDataBinding>(holder.itemView)
        if (isForDataBinding(payloads)) { // 局部更新
            binding!!.executePendingBindings()
        } else { // 完整更新
            val item = items!![position]
            onBindBinding(
                binding!!,
                xmlItemBinding.getDefaultVariableId(),
                xmlItemBinding.getLayoutRes(),
                position,
                item
            )
        }
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
     * Set the item id's for the items. If not null, this will set [ ][RecyclerView.Adapter.setHasStableIds] to true.
     */
    fun setItemIds(itemIds: ItemIds<in T>?) {
        if (this.itemIds !== itemIds) {
            this.itemIds = itemIds
            setHasStableIds(itemIds != null)
        }
    }

    /**
     * Set the factory for creating view holders. If null, a default view holder will be used. This
     * is useful for holding custom state in the view holder or other more complex customization.
     */
    fun setViewHolderFactory(factory: ViewHolderFactory?) {
        viewHolderFactory = factory
    }


    override fun getItemId(position: Int): Long {
        return if (itemIds == null) position.toLong() else itemIds!!.getItemId(
            position,
            items!![position]
        )
    }

    private fun tryGetLifecycleOwner() {
        if (lifecycleOwner == null || lifecycleOwner!!.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            lifecycleOwner = Utils.findLifecycleOwner(recyclerView)
        }
    }

    private class WeakReferenceOnListChangedCallback<T> constructor(
        adapter: BindingRecyclerViewAdapter<T>,
        items: ObservableList<T>
    ) : ObservableList.OnListChangedCallback<ObservableList<T>>() {
        internal val adapterRef: WeakReference<BindingRecyclerViewAdapter<T>>

        init {
            this.adapterRef = AdapterReferenceCollector.createRef(adapter, items, this)
        }

        override fun onChanged(sender: ObservableList<T>) {
            val adapter = adapterRef.get() ?: return
            Utils.ensureChangeOnMainThread()
            adapter.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(
            sender: ObservableList<T>,
            positionStart: Int,
            itemCount: Int
        ) {
            val adapter = adapterRef.get() ?: return
            Utils.ensureChangeOnMainThread()
            adapter.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeInserted(
            sender: ObservableList<T>,
            positionStart: Int,
            itemCount: Int
        ) {
            val adapter = adapterRef.get() ?: return
            Utils.ensureChangeOnMainThread()
            adapter.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeMoved(
            sender: ObservableList<T>,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            val adapter = adapterRef.get() ?: return
            Utils.ensureChangeOnMainThread()
            for (i in 0 until itemCount) {
                adapter.notifyItemMoved(fromPosition + i, toPosition + i)
            }
        }

        override fun onItemRangeRemoved(
            sender: ObservableList<T>,
            positionStart: Int,
            itemCount: Int
        ) {
            val adapter = adapterRef.get() ?: return
            Utils.ensureChangeOnMainThread()
            adapter.notifyItemRangeRemoved(positionStart, itemCount)
        }
    }

    private class BindingViewHolder internal constructor(binding: ViewDataBinding) :
        ViewHolder(binding.root)

    interface ItemIds<T> {
        fun getItemId(position: Int, item: T): Long
    }

    interface ViewHolderFactory {
        fun createViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder
    }

    companion object {
        private val DATA_INVALIDATION = Any()
    }
}
