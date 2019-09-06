package me.lx.rv

/**
 * Adapter执行getItemViewType()方法, 会回调该函数,用来设置每个Item的布局id和变量id
 *
 * @param <T> 每个Item的类型
</T> */
interface OnItemBind<T> {
    /**
     * 允许您修改每种 ItemBinding 对象的数据,
     * 请注意，您不应该在此方法中进行复杂处理，因为它被多次调用。
     * 在Adapter执行getItemViewType()方法, 会被多次调用。
     */
    fun onItemBind(itemBinding: XmlItemBinding<*>, position: Int, item: T)
}
