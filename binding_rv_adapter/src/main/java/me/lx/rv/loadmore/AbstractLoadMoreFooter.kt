package me.lx.rv.loadmore

import android.view.View

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/9 14:33
 *  version: 1.0
 *  desc:
 */
abstract class AbstractLoadMoreFooter {
    /**
     * 设置footer的布局
     */
    abstract fun getLayoutRes(): Int

    /**
     * footer布局初始化完成
     */
    abstract fun onCreate(footerView: View)

    /**
     * 加载更多中
     */
    abstract fun loading()

//    /**
//     * 加载完成
//     */
//    public abstract void loadComplete();

    /**
     * 加载完成-已无更多数据
     */
    abstract fun noMoreData()

    /**
     * 加载失败
     */
    abstract fun loadFailed()
}