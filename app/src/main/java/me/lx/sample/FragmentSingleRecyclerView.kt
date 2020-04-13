package me.lx.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.lx.rv.tools.Ls
import me.lx.sample.databinding.FragmentSingleRecyclerviewBinding

class FragmentSingleRecyclerView : Fragment() {
    private lateinit var mModel: MutableViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return FragmentSingleRecyclerviewBinding.inflate(inflater, container, false).also {
            initView(it)
            it.setLifecycleOwner(this)
            it.model = mModel
            it.click = mModel
            it.executePendingBindings()
        }.root
    }

    fun initView(binding: FragmentSingleRecyclerviewBinding) {
        val spinner = binding.spinnerWidth

        val datas = arrayListOf("11", "22", "33")
        // @android:id/text1
        spinner.adapter = object : ArrayAdapter<String>(
           // activity!!, android.R.layout.simple_spinner_dropdown_item,
            activity!!, R.layout.spinner_tv,
            android.R.id.text1, datas
        ) {

        }

        spinner.adapter.toString()
    }
}
