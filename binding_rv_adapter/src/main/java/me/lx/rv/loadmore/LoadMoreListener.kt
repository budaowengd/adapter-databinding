package me.lx.rv.loadmore

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/5 17:21
 *  version: 1.0
 *  desc:
 */
interface LoadMoreListener {
    /**
     * 正在加载更多中, 这里需求请求网络
     */
    fun onLoadingMore()

    /**
     * 如果没有更多数据, 会回调该方法,应该在该方法中显示没有更多内容的布局
     */
    fun onNoMore()

    /**
     * 加载更多失败, 应该在该方法中显示错误的布局
     */
    fun onLoadMoreFail()
}