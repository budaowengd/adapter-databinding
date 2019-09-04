package me.lx.rv.itembindings

import me.lx.rv.ItemBinding
import me.lx.rv.OnItemBind

/**
 * An [OnItemBind] that selects item views by delegating to each item. Items must implement
 * [ItemBindingModel].
 */
class OnItemBindModel<T : ItemBindingModel> : OnItemBind<T> {

    override fun onItemBind(itemBinding: ItemBinding<*>, position: Int, item: T) {
        item.onItemBind(itemBinding)
    }
}
