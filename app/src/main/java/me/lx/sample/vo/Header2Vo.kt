package me.lx.sample.vo

import androidx.databinding.ObservableField

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2020/1/15 14:31
 *  version: 1.0
 *  desc:
 */
class Header2Vo {
    var name: String? = "我是头"
    val nameOb = ObservableField<String>().apply { }
}