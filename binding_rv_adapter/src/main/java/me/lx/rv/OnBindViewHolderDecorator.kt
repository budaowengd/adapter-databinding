package me.lx.rv

import androidx.databinding.ViewDataBinding

/**
 * author: luoxiong
 * date :  2017/12/22
 * desc ： 普通列表装饰器
 */
interface OnBindViewHolderDecorator<T> {
    fun decorator(item: T, position: Int, vBinding: ViewDataBinding)
}