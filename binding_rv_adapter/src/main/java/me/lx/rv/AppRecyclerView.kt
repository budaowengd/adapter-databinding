package me.lx.rv

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.lx.rv.tools.Ls

/**
 * @author: luoXiong
 * @date: 2018/12/16 18:44
 * @desc:
 */
class AppRecyclerView : androidx.recyclerview.widget.RecyclerView, LifecycleObserver {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
//        if (context is LifecycleOwner) {
//            (context as LifecycleOwner).lifecycle.addObserver(this)
//        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Ls.d("AppRecyclerView..onDestroy()...88888.")
        /*
         确保Adapter#onDetachedFromRecyclerView被调用
         */
        adapter = null
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Ls.d("AppRecyclerView..onAttachedToWindow()...11111..mLastAdapter=$mLastAdapter")
        if (mLastAdapter != null) {
            adapter = mLastAdapter
        }
    }

    private var mLastAdapter: RecyclerView.Adapter<*>? = null
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mLastAdapter = adapter
        // 确保Adapter#onDetachedFromRecyclerView被调用
        adapter = null
        Ls.d("AppRecyclerView..onDetachedFromWindow()...88888.")
    }

    override fun setLayoutManager(layoutManager: LayoutManager?) {
        super.setLayoutManager(layoutManager)
        if (layoutManager is LinearLayoutManager) {
            /*
            确保Adapter#onViewDetachedFromWindow被调用
             */
            layoutManager.recycleChildrenOnDetach = true
        }
    }
}
