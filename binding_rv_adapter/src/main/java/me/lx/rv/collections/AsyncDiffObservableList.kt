package me.lx.rv.collections

import androidx.databinding.ListChangeRegistry
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import java.util.*

/**
 * Creates a new AsyncDiffObservableList of type T, which runs the diff on a background thread using
 * [AsyncListDiffer].
 *
 * @param config The config passed to `AsyncListDiffer`.
 */
class AsyncDiffObservableList<T> : AbstractList<T>, ObservableList<T> {

    private lateinit var differ: AsyncListDiffer<T>
    private val listeners = ListChangeRegistry()

    constructor(callback: DiffUtil.ItemCallback<T>) : this(AsyncDifferConfig.Builder<T>(callback).build()) {}

    constructor() : super() {

    }

    constructor(config: AsyncDifferConfig<T>) {
        differ = AsyncListDiffer<T>(ObservableListUpdateCallback(), config)
    }


    /**
     * Updates the list to the given items. A diff will run in a background thread then this
     * collection will be updated.
     */
    fun update(newItems: List<T>?) {
        differ.submitList(newItems)
    }

    override fun addOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.add(callback)
    }

    override fun removeOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.remove(callback)
    }

    override fun get(index: Int): T {
        return differ.currentList[index]
    }

    override val size: Int
        get() = differ.currentList.size

    override fun indexOf(element: T): Int {
        return differ.currentList.indexOf(element)
    }

    override fun lastIndexOf(element: T): Int {
        return differ.currentList.lastIndexOf(element)
    }


    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        return differ.currentList.subList(fromIndex, toIndex)
    }

    override fun hashCode(): Int {
        return differ.currentList.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is AsyncDiffObservableList<*>) {
            false
        } else differ.currentList == other.differ.currentList
    }


    override fun listIterator(index: Int): MutableListIterator<T> {
        return differ.currentList.listIterator(index)
    }

    internal inner class ObservableListUpdateCallback : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            listeners.notifyChanged(this@AsyncDiffObservableList, position, count)
        }

        override fun onInserted(position: Int, count: Int) {
            listeners.notifyInserted(this@AsyncDiffObservableList, position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            listeners.notifyRemoved(this@AsyncDiffObservableList, position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            listeners.notifyMoved(this@AsyncDiffObservableList, fromPosition, toPosition, 1)
        }
    }
}
