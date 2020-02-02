package me.lx.sample.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.group.GroupedGridLayoutManager
import me.lx.sample.BR
import me.lx.sample.MyApp
import me.lx.sample.R
import me.lx.sample.get
import me.lx.sample.group.model.GroupAdapterModel

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/20 11:05
 *  version: 1.0
 *  desc:
 */
class FragmentGroupList : Fragment() {
    private lateinit var mModel: GroupAdapterModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding= getViewBinding(inflater, container).also {
            it.setVariable(BR.model, mModel)
            it.setVariable(BR.click, mModel)
            it.executePendingBindings()
        }

//        if(arguments?.getString("type")==bundle_grid_child){
//          val recyclerView= binding.root.findViewById<RecyclerView>(R.id.recyclerView)
//            (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
//        }
        initLayout(binding)
        return binding.root
    }

    private fun initLayout(binding: ViewDataBinding) {
        when (arguments?.getString("type")) {
            bundle_no_header -> {
            }
            bundle_no_footer -> {
            }
            bundle_various -> {
            }
            bundle_various_child -> {
            }
            bundle_expandable -> {
            }
            bundle_grid_child -> {
            }
            bundle_grid_diff_child -> {
                val grid2LayoutManager = GroupedGridLayoutManager(MyApp.sContext, 2, mModel.groupAdapter)
                (binding.root.findViewById(R.id.recyclerView) as RecyclerView).layoutManager=grid2LayoutManager
            }

            else -> {
                R.layout.fragment_group_rv
            }
        }

    }

    private fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        val layoutId: Int = when (arguments?.getString("type")) {
            bundle_no_header -> {
                R.layout.fragment_group_rv_no_header
            }
            bundle_no_footer -> {
                R.layout.fragment_group_rv_no_footer
            }
            bundle_various -> {
                R.layout.fragment_group_rv_various
            }
            bundle_various_child -> {
                R.layout.fragment_group_rv_various_child
            }
            bundle_expandable -> {
                R.layout.fragment_group_rv_expandable
            }
            bundle_grid_child -> {
                R.layout.fragment_group_rv_grid
            }
            bundle_grid_diff_child -> {
                R.layout.fragment_group_rv_diff_grid
            }
            else -> {
                R.layout.fragment_group_rv
            }
        }
        return DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, container, false)
    }

    companion object {
        const val bundle_default = "default"
        const val bundle_no_header = "no_header"
        const val bundle_no_footer = "no_footer"
        const val bundle_various = "various"
        const val bundle_various_child = "various_child"
        const val bundle_expandable = "expandable"
        const val bundle_grid_child = "grid_child"
        const val bundle_grid_diff_child = "grid_diff_child"

        fun newInstance(type: String): FragmentGroupList {
            val bundle = Bundle()
            bundle.putString("type", type)
            val frag = FragmentGroupList()
            frag.arguments = bundle
            return frag
        }
    }

}