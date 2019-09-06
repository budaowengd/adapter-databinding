package me.lx.rv.itembindings

import me.lx.rv.OnItemBind
import me.lx.rv.XmlItemBinding

/**
 * An [OnItemBind] that selects item views by delegating to each item. Items must implement
 * [ItemBindingModel].
 */
class OnItemBindModel<T : ItemBindingModel> : OnItemBind<T> {

    override fun onItemBind(itemBinding: XmlItemBinding<*>, position: Int, item: T) {
        item.onItemBind(itemBinding)
    }
}
