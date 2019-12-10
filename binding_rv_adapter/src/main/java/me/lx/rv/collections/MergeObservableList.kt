package me.lx.rv.collections

import androidx.databinding.ListChangeRegistry
import androidx.databinding.ObservableList
import java.util.*

/**
 * An [ObservableList] that presents multiple lists and items as one contiguous source.
 * Changes to any of the given lists will be reflected here. You cannot modify `MergeObservableList` itself other than adding and removing backing lists or items with [ ][.insertItem] and [.insertList] respectively.  This is a good case
 * where you have multiple data sources, or a handful of fixed items mixed in with lists of data.
 */
class MergeObservableList<T> : AbstractList<T>(), ObservableList<T> {
    private val lists = ArrayList<List<T>>()
    private val callback = ListChangeCallback() as ObservableList.OnListChangedCallback<Nothing>
    private val listeners = ListChangeRegistry()

    override fun addOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.add(listener)
    }

    override fun removeOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.remove(listener)
    }

    /**
     * Inserts the given item into the merge list.
     */
    fun insertItem(item: T): MergeObservableList<T> {
        lists.add(listOf(item))
        modCount += 1
        listeners.notifyInserted(this, size - 1, 1)
        return this
    }

    /**
     * Inserts the given [ObservableList] into the merge list. Any changes in the given list
     * will be reflected and propagated here.
     */
    fun insertList(list: ObservableList<out T>): MergeObservableList<T> {
        list.addOnListChangedCallback(callback)
        val oldSize = size
        lists.add(list)
        modCount += 1
        if (!list.isEmpty()) {
            listeners.notifyInserted(this, oldSize, list.size)
        }
        return this
    }

    /**
     * Removes the given item from the merge list.
     */
    fun removeItem(`object`: T?): Boolean {
        var size = 0
        var i = 0
        val listsSize = lists.size
        while (i < listsSize) {
            val list = lists[i]
            if (list !is ObservableList<T>) {
                val item = list[0]
                if (if (`object` == null) item == null else `object` == item) {
                    lists.removeAt(i)
                    modCount += 1
                    listeners.notifyRemoved(this, size, 1)
                    return true
                }
            }
            size += list.size
            i++
        }
        return false
    }

    /**
     * Removes the given [ObservableList] from the merge list.
     */
    fun removeList(listToRemove: ObservableList<out T>): Boolean {
        var size = 0
        var i = 0
        val listsSize = lists.size
        while (i < listsSize) {
            val list = lists[i]
            if (list === listToRemove) {
                listToRemove.removeOnListChangedCallback(callback)
                lists.removeAt(i)
                modCount += 1
                listeners.notifyRemoved(this, size, list.size)
                return true
            }
            size += list.size
            i++
        }
        return false
    }

    /**
     * Removes all items and lists from the merge list.
     */
    fun removeAll() {
        val size = size
        if (size == 0) {
            return
        }
        var i = 0
        val listSize = lists.size
        while (i < listSize) {
            val list = lists[i]
            if (list is ObservableList<T>) {
                (list as ObservableList<T>).removeOnListChangedCallback(callback)
            }
            i++
        }
        lists.clear()
        modCount += 1
        listeners.notifyRemoved(this, 0, size)
    }

    /**
     * Converts an index into this merge list into an into an index of the given backing list.
     *
     * @throws IndexOutOfBoundsException for an invalid index.
     * @throws IllegalArgumentException  if the given list is not backing this merge list.
     */
    fun mergeToBackingIndex(backingList: ObservableList<out T>, index: Int): Int {
        if (index < 0) {
            throw IndexOutOfBoundsException()
        }
        var size = 0
        var i = 0
        val listsSize = lists.size
        while (i < listsSize) {
            val list = lists[i]
            if (backingList === list) {
                return if (index < list.size) {
                    size + index
                } else {
                    throw IndexOutOfBoundsException()
                }
            }
            size += list.size
            i++
        }
        throw IllegalArgumentException()
    }

    /**
     * Converts an index into a backing list into an index into this merge list.
     *
     * @throws IndexOutOfBoundsException for an invalid index.
     * @throws IllegalArgumentException  if the given list is not backing this merge list.
     */
    fun backingIndexToMerge(backingList: ObservableList<out T>, index: Int): Int {
        if (index < 0) {
            throw IndexOutOfBoundsException()
        }
        var size = 0
        var i = 0
        val listsSize = lists.size
        while (i < listsSize) {
            val list = lists[i]
            if (backingList === list) {
                return if (index - size < list.size) {
                    index - size
                } else {
                    throw IndexOutOfBoundsException()
                }
            }
            size += list.size
            i++
        }
        throw IllegalArgumentException()
    }

    override fun get(location: Int): T {
        if (location < 0) {
            throw IndexOutOfBoundsException()
        }
        var size = 0
        var i = 0
        val listsSize = lists.size
        while (i < listsSize) {
            val list = lists[i]
            if (location - size < list.size) {
                return list[location - size]
            }
            size += list.size
            i++
        }
        throw IndexOutOfBoundsException()
    }

    override val size: Int
        get() = getDataSize()

    private fun getDataSize(): Int {
        var size = 0
        var i = 0
        val listsSize = lists.size
        while (i < listsSize) {
            val list = lists[i]
            size += list.size
            i++
        }
        return size
    }

     inner class ListChangeCallback : ObservableList.OnListChangedCallback<ObservableList<*>>() {
        override fun onChanged(sender: ObservableList<*>) {
            modCount += 1
            listeners.notifyChanged(this@MergeObservableList)
        }

        override fun onItemRangeChanged(
            sender: ObservableList<*>,
            positionStart: Int,
            itemCount: Int
        ) {
            var size = 0
            var i = 0
            val listsSize = lists.size
            while (i < listsSize) {
                val list = lists[i]
                if (list === sender) {
                    listeners.notifyChanged(
                        this@MergeObservableList,
                        size + positionStart,
                        itemCount
                    )
                    return
                }
                size += list.size
                i++
            }
        }

        override fun onItemRangeInserted(
            sender: ObservableList<*>,
            positionStart: Int,
            itemCount: Int
        ) {
            modCount += 1
            var size = 0
            var i = 0
            val listsSize = lists.size
            while (i < listsSize) {
                val list = lists[i]
                if (list === sender) {
                    listeners.notifyInserted(
                        this@MergeObservableList,
                        size + positionStart,
                        itemCount
                    )
                    return
                }
                size += list.size
                i++
            }
        }

        override fun onItemRangeMoved(
            sender: ObservableList<*>,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            var size = 0
            var i = 0
            val listsSize = lists.size
            while (i < listsSize) {
                val list = lists[i]
                if (list === sender) {
                    listeners.notifyMoved(
                        this@MergeObservableList,
                        size + fromPosition,
                        size + toPosition,
                        itemCount
                    )
                    return
                }
                size += list.size
                i++
            }
        }

        override fun onItemRangeRemoved(
            sender: ObservableList<*>,
            positionStart: Int,
            itemCount: Int
        ) {
            modCount += 1
            var size = 0
            var i = 0
            val listsSize = lists.size
            while (i < listsSize) {
                val list = lists[i]
                if (list === sender) {
                    listeners.notifyRemoved(
                        this@MergeObservableList,
                        size + positionStart,
                        itemCount
                    )
                    return
                }
                size += list.size
                i++
            }
        }
    }
}
