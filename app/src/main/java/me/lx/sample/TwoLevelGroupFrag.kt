package me.lx.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.lx.rv.RvGroupBindListener
import me.lx.sample.databinding.FragGroupTwoLevelRvBinding
import me.lx.sample.model.TwoLevelGroupModel
import me.lx.sample.vo.SingleItemVo

class TwoLevelGroupFrag : Fragment(), ClickListeners {
    private lateinit var mModel: TwoLevelGroupModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get()
    }

    lateinit var binding: FragGroupTwoLevelRvBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragGroupTwoLevelRvBinding.inflate(inflater, container, false).also {
            it.setLifecycleOwner(this)
           /// it.rvBindGroup = mModel as RvGroupBindListener<*, *>
            it.model = mModel
            it.executePendingBindings()
        }
        return binding.root
    }

    override fun clickAddItem() {
    }

    override fun clickRemoveItem() {

    }


}
