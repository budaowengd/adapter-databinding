@file:Suppress("NOTHING_TO_INLINE")

package me.lx.rv.ext


import androidx.annotation.LayoutRes
import me.lx.rv.BR
import me.lx.rv.OnItemBind
import me.lx.rv.XmlItemBinding
import me.lx.rv.click.ClickListener

inline fun <T> itemXmlOf(@LayoutRes layoutRes: Int, clickListener: ClickListener): XmlItemBinding<T> =
        XmlItemBinding.of(layoutRes, clickListener)

inline fun <T> itemXmlOf(@LayoutRes layoutRes: Int,variableId: Int, clickListener: ClickListener): XmlItemBinding<T> =
        XmlItemBinding.of(layoutRes, variableId, clickListener)

/**
 * Creates an `XmlItemBinding` with the given id and layout.
 *
 * @see XmlItemBinding.of
 */
inline fun <T> itemXmlOf(@LayoutRes layoutRes: Int, variableId: Int = BR.item): XmlItemBinding<T> =
        XmlItemBinding.of(layoutRes, variableId)

/**
 * Creates an `XmlItemBinding` with the given callback.
 *
 * @see XmlItemBinding.of
 */
inline fun <T> itemXmlOf(onGetItemViewType: OnItemBind<T>): XmlItemBinding<T> = XmlItemBinding.of(onGetItemViewType)


/**
 * Converts an `OnItemBind` to a `XmlItemBinding`.
 *
 * @see XmlItemBinding.of
 */
inline fun <T> OnItemBind<T>.toItemBinding(): XmlItemBinding<T> =
        XmlItemBinding.of(this)

