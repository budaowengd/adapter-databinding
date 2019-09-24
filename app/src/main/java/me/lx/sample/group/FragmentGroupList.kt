package me.lx.sample.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import me.lx.sample.R
import me.lx.sample.databinding.FragmentGroupRvBinding
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
        return FragmentGroupRvBinding.inflate(inflater, container, false).also {
            it.setLifecycleOwner(this)
            it.viewModel = viewModel
            it.click = viewModel
            it.executePendingBindings()
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerView)
//        val adapter = GroupedListAdapter(GroupModel.getGroups(10, 5))
//        recyclerView.setAdapter(adapter)
    }
}