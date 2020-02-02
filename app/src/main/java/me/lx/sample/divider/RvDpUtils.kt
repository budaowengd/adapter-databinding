package me.lx.sample.divider

import android.content.res.Resources

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/12/27 21:54
 *  version: 1.0
 *  desc:
 */
class RvDpUtils {
    companion object {
        fun dp2px(dpValue: Number): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (dpValue.toFloat() * scale + 0.5f).toInt()
        }
    }
}