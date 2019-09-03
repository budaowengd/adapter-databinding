@file:Suppress("NOTHING_TO_INLINE")

package me.lx.rv.ext

import androidx.annotation.LayoutRes
import me.lx.rv.OnItemBind
import me.lx.rv.itembindings.OnItemBindClass

/**
 * Maps the given type to the given id and layout.
 *
 */
inline fun <reified T> OnItemBindClass<in T>.map(variableId: Int, @LayoutRes layoutRes: Int) {
    bindMap(T::class.java, variableId, layoutRes)
}

/**
 * Maps the given type to the given callback.
 */
inline fun <reified T> OnItemBindClass<in T>.map(onItemBind: OnItemBind<T>) {
    bindMap(T::class.java, onItemBind)
//    map(T::class.java) {itemBinding, position, item ->
//        onItemBind(
//            itemBinding as ItemBinding<in T>,
//            position,
//            item
//        )
//    }
}
