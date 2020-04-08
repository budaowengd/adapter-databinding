package me.lx.rv.bindingconvert

import androidx.databinding.BindingConversion
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import me.lx.rv.OnItemBind
import me.lx.rv.XmlItemBinding

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/2 17:56
 *  version: 1.0
 *  desc:
 */
@BindingConversion
fun <T> toItemBinding(onGetItemViewType: OnItemBind<T>?=null): XmlItemBinding<T> {
    return XmlItemBinding.of(onGetItemViewType)
}

@BindingConversion
fun <T> toAsyncDifferConfig(callback: DiffUtil.ItemCallback<T>): AsyncDifferConfig<T> {
    return AsyncDifferConfig.Builder(callback).build()
}