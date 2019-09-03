package me.lx.sample

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/2 15:18
 *  version: 1.0
 *  desc:
 */
@MainThread
inline fun <reified VM : ViewModel> ViewModelProvider.get() = get(VM::class.java)
