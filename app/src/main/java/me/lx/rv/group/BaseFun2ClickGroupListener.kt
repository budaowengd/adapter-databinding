package me.lx.rv.group

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/25 17:24
 *  version: 1.0
 *  desc:
 */
abstract class BaseFun2ClickGroupListener<G, T> : ClickGroupListener {
    abstract fun clickGroup(item: G, flag: Int)
}