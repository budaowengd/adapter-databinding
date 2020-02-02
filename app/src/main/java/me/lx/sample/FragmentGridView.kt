package me.lx.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.lx.sample.databinding.FragmentGridRecyclerviewBinding
import me.lx.sample.model.GridDividerModel

class FragmentGridView : Fragment() {
    private lateinit var mModel: GridDividerModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentGridRecyclerviewBinding.inflate(inflater, container, false).also {
            it.setLifecycleOwner(this)
            it.model = mModel
            it.executePendingBindings()
        }.root
    }
}
