package me.lx.rv.itembindings

import androidx.annotation.LayoutRes
import me.lx.rv.BR
import me.lx.rv.ItemBinding
import me.lx.rv.OnItemBind
import me.lx.rv.click.ClickListener
import java.util.*


/**
 * 如果1个列表包含多个item,可以使用该对象来创建不同的item
 * 1、多个绑定回调对象 [OnItemBind] ,和每个布局对应的变量id
 * 2、多个item对应的数据对象class
 * <pre>`itemBind = new OnItemBindClass<>()
 * .map(String.class, BR.name, R.layout.item_name)
 * .map(Footer.class, ItemBinding.VAR_NONE, R.layout.item_footer);
`</pre> *
 */
class OnItemBindClass<T> : OnItemBind<T> {

    private val itemBindingClassList: MutableList<Class<out T>> //元素: 每种item对象的vo对象
    private val itemBindingList: MutableList<OnItemBind<T>> //元素: 每种item对应的 OnItemBind 对象

    init {
        this.itemBindingClassList = ArrayList(2)
        this.itemBindingList = ArrayList(2)
    }

    /**
     * Maps the given class to the given variableId and layout. This is assignment-compatible match with the object represented by Class.
     */
    fun bindMap(
        itemClass: Class<out T>, @LayoutRes layoutRes: Int, variableId: Int = 0,
        clickListener: ClickListener? = null
    ): OnItemBindClass<T> {
        var vId = variableId
        if (variableId == 0) {
            vId = BR.item
        }
        val index = itemBindingClassList.indexOf(itemClass)
        if (index >= 0) {
            itemBindingList[index] = getOnItemBindObj(layoutRes, vId, clickListener)
        } else {
            itemBindingClassList.add(itemClass)
            itemBindingList.add(getOnItemBindObj(layoutRes, vId, clickListener))
        }
        return this
    }

    /**
     * Maps the given class to the given [OnItemBind]. This is assignment-compatible match with the object represented by Class.
     */
    fun <E : T> bindMap(itemClass: Class<E>, onItemBind: OnItemBind<E>): OnItemBindClass<T> {
        val index = itemBindingClassList.indexOf(itemClass)
        if (index >= 0) {
            itemBindingList[index] = onItemBind as OnItemBind<T>
        } else {
            itemBindingClassList.add(itemClass)
            itemBindingList.add(onItemBind as OnItemBind<T>)
        }
        return this
    }

    /**
     * Returns the number of item types in the map. This is useful for [ ][BindingListViewAdapter.BindingListViewAdapter] or `app:itemTypeCount` in an `AdapterView`.
     */
    fun itemTypeCount(): Int {
        return itemBindingClassList.size
    }

    override fun onItemBind(itemBinding: ItemBinding<*>, position: Int, item: T) {
        for (i in itemBindingClassList.indices) {
            val key = itemBindingClassList[i]
            if (key.isInstance(item)) {
                val itemBind = itemBindingList[i]
                itemBind.onItemBind(itemBinding, position, item)
                return
            }
        }
        throw IllegalArgumentException("Missing class for item $item")
    }

    private fun getOnItemBindObj(@LayoutRes layoutRes: Int, variableId: Int, clickListener: ClickListener? = null): OnItemBind<T> {
        return object : OnItemBind<T> {
            override fun onItemBind(itemBinding: ItemBinding<*>, position: Int, item: T) {
                itemBinding.set(layoutRes, variableId, clickListener)
            }
        }
    }
}
