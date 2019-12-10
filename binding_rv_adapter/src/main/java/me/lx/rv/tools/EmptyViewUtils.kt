package me.lx.rv.tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import me.lx.rv.R


/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/7/16 16:28
 *  version: 1.0
 *  desc: Activity 和 Fragment 列表空页面管理
 */
class EmptyViewUtils {
    companion object {
        fun showOrHideEmptyView(
            emptyLayoutContain: ViewGroup,
            isShowEmpty: Boolean,
            lId: Int,
            emptyTextHint: String? = null) {
            var emptyView = emptyLayoutContain.tag
            if (isShowEmpty) {
                var layoutId = lId
                if (layoutId == 0) {
                    layoutId = R.layout.rv_base_empty_list
                }
                if (emptyView == null) {
                    emptyView = createListEmptyView(emptyLayoutContain, layoutId, emptyTextHint)
                    emptyLayoutContain.tag = emptyView
                    if (!emptyTextHint.isNullOrEmpty()) {
                        emptyView.findViewById<TextView>(R.id.tvEmptyHint)?.text = emptyTextHint
                    }
                } else if (emptyView is View) {
                    emptyView.visibility = View.VISIBLE
                }
            } else if (emptyView is View) {
                emptyView.visibility = View.GONE
            }
        }

        private fun createListEmptyView(
            contain: ViewGroup,
            layoutId: Int,
            emptyTextHint: String? = null
        ): View {
            val binding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(contain.context),
                layoutId, contain, false
            )
            contain.addView(binding.root, 0)
            return binding.root
        }
    }


}