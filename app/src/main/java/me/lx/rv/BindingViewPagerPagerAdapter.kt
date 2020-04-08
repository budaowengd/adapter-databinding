package me.lx.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager.widget.PagerAdapter
import java.lang.ref.WeakReference
import java.util.*

/**
 * A [PagerAdapter] that binds items to layouts using the given [XmlItemBinding] or [ ]. If you give it an [ObservableList] it will also updated itself based on
 * changes to that list.
 */
class BindingViewPagerPagerAdapter<T> : PagerAdapter(), BindingCollectionAdapter<T> {

    private var itemBinding: XmlItemBinding<T>? = null
    private var callback: WeakReferenceOnListChangedCallback<T>? = null
    private var items: List<T>? = null
    private var inflater: LayoutInflater? = null
    private var pageTitles: PageTitles<T>? = null
    private var lifecycleOwner: LifecycleOwner? = null
    private val views = ArrayList<View>()

    override fun setItemBinding(itemBinding: XmlItemBinding<T>) {
        this.itemBinding = itemBinding
    }

    /**
     * 正常不需要自己去设置,如果View没有使用Databinding布局,就需要手动设置进来
     * Sets the lifecycle owner of this adapter to work with [androidx.lifecycle.LiveData].
     * This is normally not necessary, but due to an androidx limitation, you need to set this if
     * the containing view is *not* using databinding.
     */
    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner?) {
        this.lifecycleOwner = lifecycleOwner
        for (view in views) {
            val binding = DataBindingUtil.getBinding<ViewDataBinding>(view)
            if (binding != null) {
                binding.lifecycleOwner = lifecycleOwner
            }
        }
    }

    override fun getItemXmlObj(): XmlItemBinding<T>? {
        if (itemBinding == null) {
            throw NullPointerException("itemBinding == null")
        }
        return itemBinding
    }

    override fun setItems(items: List<T>) {
        if (this.items === items) {
            return
        }
        if (this.items is ObservableList<*>) {
            (this.items as ObservableList<T>).removeOnListChangedCallback(callback)
            callback = null
        }
        if (items is ObservableList<*>) {
            callback = WeakReferenceOnListChangedCallback(this, items as ObservableList<T>)
            items.addOnListChangedCallback(callback)
        }
        this.items = items
        notifyDataSetChanged()
    }

    override fun getAdapterItem(position: Int): T? {
        return items!![position]
    }

    override fun onCreateBinding(inflater: LayoutInflater, @LayoutRes layoutRes: Int, viewGroup: ViewGroup): ViewDataBinding {
        return DataBindingUtil.inflate(inflater, layoutRes, viewGroup, false)
    }

    override fun onBindBinding(binding: ViewDataBinding, variableId: Int, @LayoutRes layoutRes: Int, position: Int, item: T) {
        itemBinding!!.bind(binding, item)
        binding.executePendingBindings()
        if (lifecycleOwner != null) {
            binding.lifecycleOwner = lifecycleOwner
        }
    }

    /**
     * Sets the page titles for the adapter.
     */
    fun setPageTitles(pageTitles: PageTitles<T>?) {
        this.pageTitles = pageTitles
    }

    override fun getCount(): Int {
        return if (items == null) 0 else items!!.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (pageTitles == null) null else pageTitles!!.getPageTitle(position, items!![position])
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (inflater == null) {
            inflater = LayoutInflater.from(container.context)
        }
        tryGetLifecycleOwner(container)

        val item = items!![position]
        itemBinding!!.onGetItemViewType(position, item)

        val binding = onCreateBinding(inflater!!, itemBinding!!.getLayoutRes(), container)
        val view = binding.root

        onBindBinding(binding, itemBinding!!.getDefaultVariableId(), itemBinding!!.getLayoutRes(), position, item)

        container.addView(view)
        view.tag = item
        views.add(view)
        return view
    }

    private fun tryGetLifecycleOwner(view: View) {
        if (lifecycleOwner == null || lifecycleOwner!!.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            lifecycleOwner = Utils.findLifecycleOwner(view)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        views.remove(view)
        container.removeView(view)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getItemPosition(`object`: Any): Int {
        val item = (`object` as View).tag as T
        if (items != null) {
            for (i in items!!.indices) {
                if (item === items!![i]) {
                    return i
                }
            }
        }
        return PagerAdapter.POSITION_NONE
    }


    private class WeakReferenceOnListChangedCallback<T> internal
    constructor(
        adapter: BindingViewPagerPagerAdapter<T>,
        items: ObservableList<T>
    ) : ObservableList.OnListChangedCallback<ObservableList<T>>() {


        internal val adapterRef: WeakReference<BindingViewPagerPagerAdapter<T>> = AdapterReferenceCollector.createRef(adapter, items, this)

        override fun onChanged(sender: ObservableList<T>) {
            val adapter = adapterRef.get() ?: return
            Utils.ensureChangeOnMainThread()
            adapter.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
            onChanged(sender)
        }

        override fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
            onChanged(sender)
        }

        override fun onItemRangeMoved(sender: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
            onChanged(sender)
        }

        override fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
            onChanged(sender)
        }
    }

    interface PageTitles<T> {
        fun getPageTitle(position: Int, item: T): CharSequence?
    }
}
