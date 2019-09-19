package me.lx.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.lx.sample.databinding.FragmentLoadmoreRecyclerviewBinding

class FragmentLoadMoreRecyclerView : Fragment(),ClickListeners {
    private lateinit var viewModel: MutableViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding.add.text="设置下一页没有更多数据"
        binding.remove.text="设置下一页有数据"
        return binding.root
    }

    override fun clickAddItem() {
        viewModel.isNoMoreData.set(true)
    }

    override fun clickRemoveItem() {
        viewModel.isNoMoreData.set(false)
    }
}
