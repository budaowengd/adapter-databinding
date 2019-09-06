package me.lx.rv

import android.util.SparseArray

import androidx.annotation.LayoutRes
import androidx.core.util.forEach
import androidx.databinding.ViewDataBinding
import me.lx.rv.click.ClickListener

/**
 * author: luoXiong
 * e-mail: 382060748@qq.com
 * date: 2019/8/28 15:54
 * version: 1.0
 * desc: 当前对象指的是每种Item布局, 包含:布局id、绑定变量id,以及您可能想要提供的任何额外绑定的数据
 */
class XmlItemBinding<T> constructor(private val onItemBind: OnItemBind<T>?) {

    private var defaultItemVariableId: Int = BR.item // 默认绑定变量id

    private var defaultClickVariableId: Int = BR.click

    private var clickListener: ClickListener? = null
    @LayoutRes
    private var layoutRes: Int = 0 // 布局id

    private var extraBindings: SparseArray<Any>? = null // 额外的绑定变量字典

    /**
     * setVariableIdAndLayoutId
     * 设置变量id和布局id, 一般由[OnItemBind.onItemBind]进行调用
     */
    fun set(@LayoutRes layoutRes: Int, variableId: Int = 0, clickListener: ClickListener? = null): XmlItemBinding<T> {
        this.layoutRes = layoutRes
        this.clickListener = clickListener
        if (variableId == 0) {
            this.defaultItemVariableId = BR.item
        } else {
            this.defaultItemVariableId = variableId
        }
        return this
    }

    /**
     * 设置变量id和布局id, 一般由[OnItemBind.onItemBind]进行调用
     */
    fun set(@LayoutRes layoutRes: Int, clickListener: ClickListener): XmlItemBinding<T> {
        this.layoutRes = layoutRes
        this.clickListener = clickListener
        this.defaultItemVariableId = BR.item
        return this
    }

    /**
     * 设置变量id, 一般由[OnItemBind.onItemBind]进行调用
     */
    fun setVariableId(variableId: Int): XmlItemBinding<T> {
        this.defaultItemVariableId = variableId
        return this
    }


    /**
     * 设置布局id, 一般由[OnItemBind.onItemBind]进行调用
     */
    fun setLayoutRes(@LayoutRes layoutRes: Int): XmlItemBinding<T> {
        this.layoutRes = layoutRes
        return this
    }

    /**
     * 添加额外的变量, 绑定到视图布局.
     * 比如: 1个item需要多个变量id,可以通过该方法进行绑定
     */
    fun bindExtra(variableId: Int, value: Any): XmlItemBinding<T> {
        if (extraBindings == null) {
            extraBindings = SparseArray(1)
        }
        extraBindings!!.put(variableId, value)
        return this
    }

    /**
     * 清除额外的变量绑定, 一般在 [OnItemBind.onItemBind]被调用
     */
    fun clearExtras(): XmlItemBinding<T> {
        if (extraBindings != null) {
            extraBindings!!.clear()
        }
        return this
    }

    /**
     * 移除额外的变量绑定, 一般在 [OnItemBind.onItemBind]被调用
     */
    fun removeExtra(variableId: Int): XmlItemBinding<T> {
        if (extraBindings != null) {
            extraBindings!!.remove(variableId)
        }
        return this
    }

    /**
     * 返回当前布局绑定的变量id
     */
    fun getDefaultVariableId(): Int {
        return defaultItemVariableId
    }

    /**
     *返回当前布局id
     */
    @LayoutRes
    fun getLayoutRes(): Int {
        return layoutRes
    }

    /**
     * 返回给定变量id的绑定对象，如果不存在，则返回null。
     */
    fun extraBinding(variableId: Int): Any? {
        return if (extraBindings == null) {
            null
        } else extraBindings!!.get(variableId)
    }

    /**
     * 更新给定项目和位置的绑定状态。 这由绑定集合适配器在内部调用.
     */
    fun onItemBind(position: Int, item: T) {
        if (onItemBind != null) {
            defaultItemVariableId = VAR_INVALID
            layoutRes = LAYOUT_NONE
            onItemBind.onItemBind(this, position, item)
            check(defaultItemVariableId != VAR_INVALID) { "defaultItemVariableId not set in onItemBind()  defaultItemVariableId=$defaultItemVariableId" }
            check(layoutRes != LAYOUT_NONE) { "layoutRes not set in onItemBind()" }
        }
    }

    /**
     *将项目和额外绑定绑定到给定的绑定。 如果有任何绑定，则返回true
     *否则为假。 这由绑定集合适配器在内部调用。
     * @throws IllegalStateException 如果该变量id不在布局当中
     */
    fun bind(binding: ViewDataBinding, item: T): Boolean {
        if (defaultItemVariableId == VAR_NONE) {
            return false
        }
        val result = binding.setVariable(defaultItemVariableId, item)

        if (clickListener != null) {
            binding.setVariable(defaultClickVariableId, clickListener)
        }

        if (!result) {
            Utils.throwMissingVariable(binding, defaultItemVariableId, layoutRes)
        }
        if (extraBindings != null) {
            extraBindings!!.forEach { key, value ->
                if (key != VAR_NONE) {
                    binding.setVariable(key, value)
                }
            }
        }
        return true
    }

    companion object {

        /**
         * 如果布局不需要变量, 使用此常量作为变量id,将不会被绑定到布局中
         * 如:静态页脚。
         */
        const val VAR_NONE = 0
        private const val VAR_INVALID = -1
        private const val LAYOUT_NONE = 0

        /**
         *使用给定的变量id和layout构造一个实例。
         */
        fun <T> of(variableId: Int, @LayoutRes layoutRes: Int): XmlItemBinding<T> {
            return XmlItemBinding<T>(null).set(layoutRes, variableId)
        }

        /**
         *使用给定的变量id和layout构造一个实例。
         */
        fun <T> of(@LayoutRes layoutRes: Int, clickListener: ClickListener): XmlItemBinding<T> {
            return XmlItemBinding<T>(null).set(layoutRes, clickListener = clickListener)
        }


        /**
         * 使用给定的回调构造一个实例。 它将被调用为每个item布局设置绑定信息。
         *
         * @see OnItemBind
         */
        @JvmStatic
        fun <T> of(onItemBind: OnItemBind<T>): XmlItemBinding<T> {
            return XmlItemBinding(onItemBind)
        }
    }
}
