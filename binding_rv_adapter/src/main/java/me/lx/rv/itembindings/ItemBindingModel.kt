package me.lx.rv.itembindings

import me.lx.rv.ItemBinding


/**
 * Implement this interface on yor items to use with [OnItemBindModel].
 */
interface ItemBindingModel {
    /**
     * Set the binding variable and layout of the given view.
     * <pre>`onItemBind.set(BR.item, R.layout.item);
    `</pre> *
     */
    fun onItemBind(itemBinding: ItemBinding<*>)
}
