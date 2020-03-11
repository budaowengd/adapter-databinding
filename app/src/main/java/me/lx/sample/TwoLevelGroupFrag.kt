package me.lx.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.lx.rv.tools.Ls
import me.lx.sample.databinding.FragGroupTwoLevelRvBinding
import me.lx.sample.group.entity.ChildGroupEntity
import me.lx.sample.group.model.GroupModel
import me.lx.sample.model.TwoLevelGroupModel

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
            it.click = this@TwoLevelGroupFrag
            it.executePendingBindings()
        }

        mModel.childGroupFooterClickCallback = Consumer { childGroup ->
            var findChildGroup: ChildGroupEntity? = null
            mModel.groupList.forEach { group ->
                group.childGroupList.forEach {
                    if (it == childGroup) {
                        group.childGroupList.remove(childGroup)
                        Ls.d("暗暗..11111111111")
                        return@Consumer
                    }
                }
            }
            Ls.d("暗暗..22222222")
        }

        return binding.root
    }

    override fun clickAddItem() {
        GroupModel
        val childSize = mModel.groupList[0].childGroupList.size

        val cgList = ChildGroupEntity()
        val cg = GroupModel.getChildGroupEntity(0, childSize, 3)
        mModel.groupList[0].childGroupList.add(0, cg)
        // .childChildList.add(ChildChildEntity((size + 1).toString()))
    }

    override fun clickRemoveItem() {
        val groupList = mModel.groupList
        Ls.d("groupList=${groupList.size}")
        val childGroup0 = groupList[0]
        Ls.d("childGroupList=${childGroup0.childGroupList.size}")
        val size = childGroup0.childGroupList[0].childChildList.size


        mModel.groupList[0].childGroupList[0].childChildList.removeAt(size - 1)
        if (size == 1) {
            // mModel.groupList[0].childGroupList.removeAt(0)
        }
    }


}
