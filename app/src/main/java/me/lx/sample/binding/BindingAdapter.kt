package me.lx.sample.binding

import android.view.View
import androidx.databinding.BindingAdapter
import me.lx.rv.tools.Ls

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2020/1/18 08:23
 *  version: 1.0
 *  desc:
 */
@BindingAdapter(value=["cbind3_test"],requireAll = false)
fun  bind3_test(
    rv: View,
    bind1_test: Int = 0
) {
    Ls.d("bind..3_test()...333..bind1_test=$bind1_test")

}
@BindingAdapter(value=["abind1_test"],requireAll = false)
fun  bind1_test(
    rv: View,
    bind1_test: Int = 0
) {
    Ls.d("bind..1_test()...111..bind1_test=$bind1_test")

}
@BindingAdapter(value=["bbind2_test"],requireAll = false)
fun  bind2_test(
    rv: View,
    bind2_test: Int = 0
) {
    Ls.d("bind..2_test()...222..bind2_test=$bind2_test")

}