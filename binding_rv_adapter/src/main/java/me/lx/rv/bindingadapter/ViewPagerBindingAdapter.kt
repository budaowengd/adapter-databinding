package me.lx.rv.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager
import me.lx.rv.BindingViewPagerPagerAdapter
import me.lx.rv.XmlItemBinding

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/11/18 17:09
 *  version: 1.0
 *  desc:
 */
/**
app:viewPager_item_binding="@{itemBinding}"
app:viewPager_items="@{items}"
app:viewPager_titles="@{titles}"
app:viewPager_limit="@{3}"
 * 设置ViewPager的 PagerAdapter
 */
@BindingAdapter(value = ["viewPager_item_binding", "viewPager_items", "viewPager_titles", "viewPager_limit"], requireAll = false)
fun <T> set_viewpager_pagerAdapter(viewPager: ViewPager, itemBinding: XmlItemBinding<T>, items: List<T>,
                                   pageTitles: BindingViewPagerPagerAdapter.PageTitles<T>?,
                                   limit: Int? = null) {
    if (viewPager.adapter != null) {
        return
    }
    requireNotNull(itemBinding) { "onItemBind must not be null" }
    val adapter = BindingViewPagerPagerAdapter<T>()
    // val oldAdapter = viewPager.adapter as BindingViewPagerPagerAdapter<T>?
    adapter.setItemBinding(itemBinding)
    adapter.setItems(items)
    adapter.setPageTitles(pageTitles)
    if (limit != null && limit > 0) {
        viewPager.offscreenPageLimit = limit
    }
    viewPager.adapter = adapter
}