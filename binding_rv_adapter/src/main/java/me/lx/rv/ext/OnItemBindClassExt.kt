@file:Suppress("NOTHING_TO_INLINE")

package me.lx.rv.ext

import androidx.annotation.LayoutRes
import me.lx.rv.BR
import me.lx.rv.OnItemBind
import me.lx.rv.click.ClickListener
import me.lx.rv.itembindings.OnItemBindClass

/**
 * Maps the given type to the given id and layout.
 *
 */
inline fun <reified T> OnItemBindClass<in T>.map(@LayoutRes layoutRes: Int, variableId: Int = BR.item) {
    bindMap(T::class.java, layoutRes, variableId)
}

inline fun <reified T> OnItemBindClass<in T>.map(@LayoutRes layoutRes: Int, clickListener: ClickListener,itemVariableId: Int = BR.item) {
    bindMap(T::class.java, layoutRes, itemVariableId, clickListener)
}

/**
 * Maps the given type to the given callback.
 */
inline fun <reified T> OnItemBindClass<in T>.map(onItemBind: OnItemBind<T>) {
    bindMap(T::class.java, onItemBind)
//    map(T::class.java) {itemBinding, position, item ->
//        onItemBind(
//            itemBinding as XmlItemBinding<in T>,
//            position,
//            item
//        )
//    }
}
