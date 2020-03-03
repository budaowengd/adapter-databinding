package me.lx.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.loadmore.LoadMoreAdapter
import me.lx.rv.tools.Ls
import me.lx.sample.databinding.FragmentLoadmoreRecyclerviewBinding
import me.lx.sample.model.LoadMoreModel
import me.lx.sample.vo.SingleItemVo

class FragmentLoadMoreRecyclerView : Fragment(), ClickListeners {
    private lateinit var mModel: LoadMoreModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoadMoreAdapter.DEFAULT_FOOTER_PATH = "me.lx.sample.loadmore.CustomLoadMoreFooter"
        mModel = ViewModelProviders.of(this).get()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoadmoreRecyclerviewBinding.inflate(inflater, container, false).also {
            it.setLifecycleOwner(this)
            it.model = mModel
            it.click = this@FragmentLoadMoreRecyclerView
            it.executePendingBindings()
        }
        binding.add.text = "设置下一页没有/有更多数据"
        binding.remove.text = "设置下一页加载失败/成功"
        initView(binding)
        return binding.root
    }

    private fun initView(binding: FragmentLoadmoreRecyclerviewBinding?) {
        binding!!.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Ls.d("onScrollStateChanged()...3333...newState=$newState")
            }
        })
    }

    override fun clickBtn3() {
        // mModel.isShowNoMoreData.set(!mModel.isShowNoMoreData.get())

        mModel.singleItems.clear()
        mModel.adapter.notifyDataSetChanged()
    }

    override fun clickAddItem() {
//        mModel.isShowNoMoreData.set(!mModel.isShowNoMoreData.get())
//        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerView)
//        recyclerView.scrollBy(0,13)
//        recyclerView.stopScroll()
        mModel.singleItems.add(SingleItemVo(mModel.singleItems.size))
        mModel.isShowNoMoreData.set(true)
    }

    override fun clickRemoveItem() {
//        mModel.isLoadMoreFail=!mModel.isLoadMoreFail

        if (mModel.singleItems.isNotEmpty()) {
            mModel.singleItems.removeAt(0)
        }
    }
}
