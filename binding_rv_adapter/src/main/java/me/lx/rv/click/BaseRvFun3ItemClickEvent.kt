package me.lx.rv.click

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/3 11:20
 *  version: 1.0
 *  desc: 普通列表默认点击实现, 包含2个参数
 */
abstract class BaseRvFun3ItemClickEvent<One, Two, Three> : ClickListener {
    abstract fun clickRvItem(view: One, item: Two, flag: Three)
}
