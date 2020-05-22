package me.lx.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.lx.rv.RvGroupBindListener
import me.lx.rv.RvThreeLevelGroupBindListener
import me.lx.rv.tools.Ls
import me.lx.sample.databinding.FragGroupTwoLevelRvBinding
import me.lx.sample.group.entity.TwoLevelGroupEntity
import me.lx.sample.group.entity.TwoLevelGroupEntity.CgEntity
import me.lx.sample.group.entity.TwoLevelGroupEntity.CgEntity.CcEntity
import me.lx.sample.group.model.GroupModel
import me.lx.sample.model.ThreeLevelGroupModel

class ThreeLevelGroupFrag : Fragment(), ClickListeners {
    private lateinit var mModel: ThreeLevelGroupModel
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
            it.rvBindGroup = mModel as RvThreeLevelGroupBindListener<Any, Any, Any>
            it.model = mModel
            it.click = this@ThreeLevelGroupFrag
            it.executePendingBindings()
        }



        return binding.root
    }

    override fun clickAddItem() {
        if (mModel.groupList.isNotEmpty() && mModel.groupList.first().childGroupList.isNotEmpty()) {
            if (mModel.groupList.first().childGroupList.isNotEmpty()) {
                val ccList = mModel.groupList.first().childGroupList.first().childChildList
                val cc = GroupModel.createCc(0, 0, ccList.size)
                ccList.add(cc)
            }
        }
    }

    override fun clickRemoveItem() {
        if (mModel.groupList.isNotEmpty()) {
            if (mModel.groupList.first().childGroupList.isNotEmpty()
                && mModel.groupList.first().childGroupList.first().childChildList.isNotEmpty()) {
                val size=mModel.groupList.first().childGroupList.first().childChildList.size
                mModel.groupList[0].childGroupList[0].childChildList.removeAt(size - 1)
            }
        }
    }


}
