package me.lx.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.loadmore.LoadMoreAdapter
import me.lx.sample.databinding.FragmentLoadmoreRecyclerviewBinding

class FragmentLoadMoreRecyclerView : Fragment(),ClickListeners {
    private lateinit var viewModel: MutableViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoadMoreAdapter.DEFAULT_FOOTER_PATH="me.lx.sample.loadmore.CustomLoadMoreFooter"
        viewModel = ViewModelProviders.of(this).get()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val binding =  FragmentLoadmoreRecyclerviewBinding.inflate(inflater, container, false).also {
            it.setLifecycleOwner(this)
            it.viewModel = viewModel
            it.click = this@FragmentLoadMoreRecyclerView
            it.executePendingBindings()
        }
        binding.add.text="设置下一页没有/有更多数据"
        binding.remove.text="设置下一页加载失败/成功"
        println("FragmentLoadMoreRecyclerView....onCreateView().....viewModel=${viewModel.hashCode()}")
        return binding.root
    }

    override fun clickAddItem() {
        viewModel.isShowNoMoreData.set(!viewModel.isShowNoMoreData.get())
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.scrollBy(0,13)
        recyclerView.stopScroll()
    }

    override fun clickRemoveItem() {
        viewModel.isLoadMoreFail=!viewModel.isLoadMoreFail

    }
}
