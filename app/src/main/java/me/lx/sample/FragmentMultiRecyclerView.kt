package me.lx.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.lx.sample.databinding.FragmentMultiRecyclerviewBinding
import me.lx.sample.vo.SingleItemVo

class FragmentMultiRecyclerView : Fragment() ,ClickListeners{
    private lateinit var mModel: MutableViewModel
    override fun clickAddItem() {
        mModel.multiItems.apply {
            insertItem(SingleItemVo(size))
        }
    }
    override fun clickRemoveItem() {
        mModel.clickRemoveItem()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentMultiRecyclerviewBinding.inflate(inflater, container, false).also {
            it.setLifecycleOwner(this)
            it.model = mModel
            it.click = this@FragmentMultiRecyclerView
            it.executePendingBindings()
        }.root
    }
}
