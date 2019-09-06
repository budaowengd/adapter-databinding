package me.lx.rv.load

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.R

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/6 09:01
 *  version: 1.0
 *  desc:
 */
class LoadAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var inflater: LayoutInflater? = null
    @LayoutRes
    private var loadMoreLayoutId = R.layout.rv_load_more_layout

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val loadMoreBinding = DataBindingUtil.inflate(inflater!!, loadMoreLayoutId, parent, false) as ViewDataBinding
        return object : RecyclerView.ViewHolder(loadMoreBinding.root) {}
    }

    override fun getItemCount(): Int {
       return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val type_1=1
    val type_2=1
    val type_3=1



}