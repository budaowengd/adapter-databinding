package me.lx.sample.loadmore

import android.view.View
import android.widget.TextView
import me.lx.rv.loadmore.AbstractLoadMoreFooter
import me.lx.sample.R

/**
 * 自定义底部加载布局
 */
class CustomLoadMoreFooter : AbstractLoadMoreFooter() {
    private lateinit var mTvText: TextView

    override fun getLayoutRes(): Int {
        return R.layout.load_more_custom_layout
    }

    override fun onCreate(footerView: View) {
        mTvText = footerView.findViewById(R.id.tvText)
    }

    override fun loading() {
        mTvText.text = "正在加载中…"
    }

    override fun noMoreData() {
        mTvText.text = "没有更多数据了"
    }

    override fun loadFailed() {
        mTvText.text = "加载失败,点击重试"
    }
}
