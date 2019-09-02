package me.lx.rv

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.annotation.LayoutRes
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding

/**
 * author: luoXiong
 * e-mail: 382060748@qq.com
 * date: 2019/8/28 15:54
 * version: 1.0
 * desc: 每个适配器都需要实现该接口
 */
interface BindingCollectionAdapter<T> {

    /**
     * 返回ItemBinding对象从适配器里
     */
    /**
     * 设置 ItemBinding 对象到适配器
     */
    fun setItemBinding(itemBinding: ItemBinding<T>)

    fun getItemBinding(): ItemBinding<T>

    /**
     * 设置适配器的项目。 这些项目将根据[ItemBinding]显示。 如果
     * 您传入[ObservableList]，适配器也将根据它自动更新。不需要手动调用 `notifyDataSetChanged()`
     *
     *
     * 请注意，适配器将直接引用给定的列表。
     * 它的任何更改*必须* em>在主线程上发生。
     * 此外，如果您不使用`ObservableList`，你*必须* em>调用`notifyDataSetChanged()`或其他相关方法。
     */
    fun setItems(items: List<T>)

    /**
     * 返回适配器给定位置的项目
     */
    fun getAdapterItem(position: Int): T

    /**
     * Adapter中执行 onCreateViewHolder() 方法的回调,支持子类进行覆盖转换特定的布局绑定和获取视图字段
     */
    fun onCreateBinding(
        inflater: LayoutInflater, @LayoutRes layoutRes: Int,
        viewGroup: ViewGroup
    ): ViewDataBinding

    /**
     * Adapter中执行 onBindViewHolder() 方法的回调
     */
    fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int, @LayoutRes layoutRes: Int,
        position: Int,
        item: T
    )
}
