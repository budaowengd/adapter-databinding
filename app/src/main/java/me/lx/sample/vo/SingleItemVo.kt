package me.lx.sample.vo

import androidx.annotation.MainThread
import androidx.databinding.ObservableBoolean

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/8/28 14:06
 *  version: 1.0
 *  desc: 比如:这是服务器返回的json对象
 */
class SingleItemVo(
    val index: Int,
    var isCheckedOb: ObservableBoolean = ObservableBoolean()
) {

    @MainThread
    fun onToggleChecked(): Boolean {
        isCheckedOb.set(!isCheckedOb.get())
        return true
    }
}
