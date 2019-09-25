package me.lx.sample.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.lx.sample.BR
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
    private lateinit var viewModel: GroupAdapterModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return getViewBinding(inflater, container).also {
            it.setVariable(BR.viewModel, viewModel)
            it.setVariable(BR.click, viewModel)
            it.executePendingBindings()
        }.root
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

        fun newInstance(type: String): FragmentGroupList {
            val bundle = Bundle()
            bundle.putString("type", type)
            val frag = FragmentGroupList()
            frag.arguments = bundle
            return frag
        }
    }

}