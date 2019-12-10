package me.lx.rv

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/11/18 17:51
 *  version: 1.0
 *  desc:
 */
interface ViewPagerBindListener<T> {
    /**
     * 获取item的布局文件
     */
    fun getItemBinding(): XmlItemBinding<T>

    /**
     * 获取列表数据源
     */
    fun getItems(): java.util.AbstractList<T>

    fun getViewPagerLimit():Int?{
        return null
    }

    fun getTitles(): BindingViewPagerPagerAdapter.PageTitles<T>? {
        return null
    }
}