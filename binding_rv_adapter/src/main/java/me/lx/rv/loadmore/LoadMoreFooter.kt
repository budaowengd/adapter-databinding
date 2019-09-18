package me.lx.rv.loadmore

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

import me.lx.rv.R

/**
 * 默认的底部加载布局
 */
internal class LoadMoreFooter : AbstractLoadMoreFooter() {

    private lateinit var mTvText: TextView

    override fun setLayoutRes(): Int {
        return R.layout.rv_load_more_layout
    }

    override fun onCreate(footerView: View) {
        mTvText = footerView.findViewById(R.id.tvText)
    }

    override fun loading() {
        mTvText.text = mTvText.context.getString(R.string.load_more_default)
    }

    override fun noMoreData() {
        mTvText.text = mTvText.context.getString(R.string.load_more_no_more_data)
    }

    override fun loadFailed() {
        mTvText.text = mTvText.context.getString(R.string.load_more_fail)
    }

}
