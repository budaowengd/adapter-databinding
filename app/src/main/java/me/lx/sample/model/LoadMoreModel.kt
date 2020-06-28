package me.lx.sample.model

import android.os.Handler
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import me.lx.rv.BindingRecyclerViewAdapter
import me.lx.rv.ext.itemXmlOf
import me.lx.rv.loadmore.LoadMoreAdapter
import me.lx.sample.R
import me.lx.sample.vo.SingleItemVo

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2020/1/17 14:06
 *  version: 1.0
 *  desc:
 */
class LoadMoreModel : ViewModel() {
    // 数据 -> item
    val singleItems = ObservableArrayList<SingleItemVo>().apply {
        for (i in 0 until 10) {
            add(SingleItemVo(i))
        }
    }
    // 适配器
    val adapter = BindingRecyclerViewAdapter<SingleItemVo>()

    // 布局 ->单一的
    val simpleItemBinding = itemXmlOf<SingleItemVo>(R.layout.item_single)

    // 加载更多相关
    var isLoadMoreFail=false
    var isShowNoMoreData = ObservableBoolean() // 加载更多后,没有更多数据的标识
    var isShowLoadMoreFailOb = ObservableBoolean() // 加载更多是否请求失败的标识
    val loadMoreListener = object : LoadMoreAdapter.LoadMoreListener {
        override fun onLoadingMore() {
            //Ls.d("onLoadingMore()..请求网络..22222....当前size=${singleItems.size} isLoadMoreFail=$isLoadMoreFail")
            Handler().postDelayed({
                isShowLoadMoreFailOb.set(isLoadMoreFail)
                if (!isLoadMoreFail) {
                    for (i in singleItems.size until singleItems.size + 2) {
                        singleItems.add(SingleItemVo(i))
                    }
                }
            }, 500)
        }

        override fun isShowNoMoreDataOb(): ObservableBoolean {
            return isShowNoMoreData
        }

        override fun isShowLoadMoreFailOb(): ObservableBoolean {
            return isShowLoadMoreFailOb
        }
    }

}