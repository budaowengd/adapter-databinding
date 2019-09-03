package me.lx.rv.click

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/3 11:20
 *  version: 1.0
 *  desc:
 */
abstract class BaseItemClickEvent<T> : ClickListener{
    abstract fun onItemClick(item: T)
}
