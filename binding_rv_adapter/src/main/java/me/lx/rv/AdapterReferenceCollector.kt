package me.lx.rv

import androidx.databinding.ObservableList
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

internal class AdapterReferenceCollector {
    companion object {
        private val QUEUE = ReferenceQueue<Any>()
        private var sThread: PollReferenceThread? = null
        /**
         * 创建一个[WeakReference]，它将在收集适配器时从给定的observable列表中取消注册给定的回调。
         *      
         */
        fun <T,A : BindingCollectionAdapter<T>> createRef(
          //  adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            adapter: A,
            items: ObservableList<T>,
            callback: ObservableList.OnListChangedCallback<ObservableList<T>>
        ): WeakReference<A> {
            if (sThread == null || !sThread!!.isAlive) {
                sThread = PollReferenceThread()
                sThread!!.start()
            }
            return AdapterRef(adapter, items, callback)
        }


        class PollReferenceThread : Thread() {
            override fun run() {
                while (true) {
                    try {
                        val ref = QUEUE.remove()
                        if (ref is AdapterRef<*, *>) {
                            ref.unregister()
                        }
                    } catch (e: InterruptedException) {
                        break
                    }

                }
            }
        }

        internal class AdapterRef<T,A : BindingCollectionAdapter<T>>(
           // referent: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            referent: A,
            private val items: ObservableList<T>,
            private val callback: ObservableList.OnListChangedCallback<ObservableList<T>>
        ) : WeakReference<A>(referent, QUEUE) {

            fun unregister() {
                items.removeOnListChangedCallback(callback)
            }
        }
    }
}
