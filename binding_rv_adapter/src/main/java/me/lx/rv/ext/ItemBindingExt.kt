@file:Suppress("NOTHING_TO_INLINE")

package me.lx.rv.ext


import androidx.annotation.LayoutRes
import me.lx.rv.BR
import me.lx.rv.ItemBinding
import me.lx.rv.OnItemBind
import me.lx.rv.click.ClickListener

inline fun <T> itemBindingOf(@LayoutRes layoutRes: Int, clickListener: ClickListener): ItemBinding<T> =
    ItemBinding.of(layoutRes, clickListener)



/**
 * Creates an `ItemBinding` with the given id and layout.
 *
 * @see ItemBinding.of
 */
inline fun <T> itemBindingOf(@LayoutRes layoutRes: Int,variableId: Int= BR.item): ItemBinding<T> =
    ItemBinding.of(variableId, layoutRes)

/**
 * Creates an `ItemBinding` with the given callback.
 *
 * @see ItemBinding.of
 */
inline fun <T> itemBindingOf(onItemBind: OnItemBind<T>): ItemBinding<T> = ItemBinding.of(onItemBind)


/**
 * Converts an `OnItemBind` to a `ItemBinding`.
 *
 * @see ItemBinding.of
 */
inline fun <T> OnItemBind<T>.toItemBinding(): ItemBinding<T> =
    ItemBinding.of(this)

