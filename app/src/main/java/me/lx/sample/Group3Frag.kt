package me.lx.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.lx.rv.RvBindGroup3Listener
import me.lx.rv.click.ClickListener
import me.lx.rv.group.BaseFun2ClickGroupListener
import me.lx.rv.group.ClickGroupListener
import me.lx.rv.group.Group3RecyclerViewAdapter
import me.lx.sample.databinding.RvGroup3Binding
import me.lx.sample.group.adapter.Group3TestAdapter
import me.lx.sample.group.entity.TwoLevelGroupEntity
import me.lx.sample.group.model.GroupModel
import me.lx.sample.model.ThreeLevelGroupModel
import java.util.AbstractList

class Group3Frag : Fragment(), ClickListeners,RvBindGroup3Listener<TwoLevelGroupEntity, TwoLevelGroupEntity.CgEntity, TwoLevelGroupEntity.CgEntity.CcEntity>{
    companion object {
        const val click_g_header = 1
        const val click_g_footer = 2
        const val click_cc = 3
        const val click_cg_header = 4
        const val click_cg_footer = 5
    }

    private lateinit var mModel: ThreeLevelGroupModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get()
    }

    lateinit var binding: RvGroup3Binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RvGroup3Binding.inflate(inflater, container, false).also {
            it.setLifecycleOwner(this)
            it.rvBindGroup3 = this as RvBindGroup3Listener<Any, Any, Any>
            //it.click = this@Group3Frag
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

    val gHeaderFooterClick = object : BaseFun2ClickGroupListener<TwoLevelGroupEntity, Int>() {
        override fun clickGroup(item: TwoLevelGroupEntity, flag: Int) {
            when (flag) {
                Group3Frag.click_g_header -> {
                    Toast.makeText(MyApp.sContext, item.headerText, Toast.LENGTH_SHORT).show()
                }
                Group3Frag.click_g_footer -> {
                    Toast.makeText(MyApp.sContext, item.footerText, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val cgHeaderFooterClick = object : BaseFun2ClickGroupListener<TwoLevelGroupEntity.CgEntity, Int>() {
        override fun clickGroup(item: TwoLevelGroupEntity.CgEntity, flag: Int) {
            when (flag) {
                Group3Frag.click_cg_header -> {
                    Toast.makeText(MyApp.sContext, item.cgHeaderText, Toast.LENGTH_SHORT).show()
                }
                Group3Frag.click_cg_footer -> {
                    Toast.makeText(MyApp.sContext, item.cgFooterText, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val ccClick = object : BaseFun2ClickGroupListener<TwoLevelGroupEntity.CgEntity.CcEntity, Int>() {
        override fun clickGroup(item: TwoLevelGroupEntity.CgEntity.CcEntity, flag: Int) {
            Toast.makeText(MyApp.sContext, item.childChildText, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getGroupList(): AbstractList<TwoLevelGroupEntity> {
        return mModel.groupList
    }

    override fun getGroupAdapter(): Group3RecyclerViewAdapter<TwoLevelGroupEntity, TwoLevelGroupEntity.CgEntity, TwoLevelGroupEntity.CgEntity.CcEntity> {
        return Group3TestAdapter()
    }

    override fun getClickCgHeaderFooterListener(): ClickListener? {
        return cgHeaderFooterClick
    }

    override fun getClickHeaderListener(): ClickListener? {
        return gHeaderFooterClick
    }

    override fun getClickFootListener(): ClickListener? {
        return gHeaderFooterClick
    }

    override fun getClickCcListener(): ClickGroupListener? {
        return ccClick
    }
}
