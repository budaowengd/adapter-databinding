package me.lx.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.lx.rv.tools.Ls
import me.lx.sample.databinding.FragSwitchSpanBinding
import me.lx.sample.model.SwitchSpanModel
import me.lx.sample.vo.SingleItemVo

class FragSwitchSpanRv : Fragment(), ClickListeners {
    private lateinit var mModel: SwitchSpanModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get()
    }

    lateinit var binding: FragSwitchSpanBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragSwitchSpanBinding.inflate(inflater, container, false).also {
            it.setLifecycleOwner(this)
            Ls.d("FragSwitchSpanRv..onCreateView()...111...model=${mModel.hashCode()}  layout=${it.recyclerView.layoutManager}")
            it.model = mModel
            it.click = this@FragSwitchSpanRv
            it.executePendingBindings()
        }
        Ls.d("FragSwitchSpanRv..onCreateView()...222...model=${mModel.hashCode()}  layout=${binding.recyclerView.layoutManager}")
        return binding.root
    }

    override fun clickAddItem() {
        mModel.singleItems.add(SingleItemVo(index = mModel.singleItems.size))
    }

    var flag=false
    override fun clickRemoveItem() {
        if(flag){
            mModel.layout.set(1)
            mModel.adapter.setItemBinding(mModel.simpleItemBinding)
        }else{
            mModel.layout.set(2)
            mModel.adapter.setItemBinding(mModel.simpleGridItemBinding)
        }

        flag=!flag
//        val items=arrayListOf<SingleItemVo>()
//        items.addAll(mModel.singleItems)
//        mModel.singleItems.clear()
//        mModel.singleItems.addAll(items)
        //binding.model = mModel
        //mModel.adapter.notifyDataSetChanged()


      //  mModel.adapter.notifyDataSetChanged()
        if (mModel.singleItems.size > 1) {
            //mModel.singleItems.removeAt(mModel.singleItems.size - 1)

        }

    }

}
