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
class ItemBinding<T> constructor(private val onItemBind: OnItemBind<T>?) {
    private var variableId: Int = 0  // 默认绑定变量id

    private var defaultItemVariableId: Int? = null

    private var defaultClickVariableId: Int? = null

    private var clickListener: ClickListener? = null
    @LayoutRes
    private var layoutRes: Int = 0 // 布局id
    private var extraBindings: SparseArray<Any>? = null // 额外的绑定变量字典

    /**
     * 设置变量id和布局id, 一般由[OnItemBind.onItemBind]进行调用
     */
    fun set(variableId: Int, @LayoutRes layoutRes: Int): ItemBinding<T> {
        this.variableId = variableId
        this.layoutRes = layoutRes
        return this
    }

    /**
     * 设置变量id, 一般由[OnItemBind.onItemBind]进行调用
     */
    fun setVariableId(variableId: Int): ItemBinding<T> {
        this.variableId = variableId
        return this
    }

    /**
     * 设置布局id, 一般由[OnItemBind.onItemBind]进行调用
     */
    fun setLayoutRes(@LayoutRes layoutRes: Int): ItemBinding<T> {
        this.layoutRes = layoutRes
        return this
    }

    /**
     * 添加额外的变量, 绑定到视图布局.
     * 比如: 1个item需要多个变量id,可以通过该方法进行绑定
     */
    fun bindExtra(variableId: Int, value: Any): ItemBinding<T> {
        if (extraBindings == null) {
            extraBindings = SparseArray(1)
        }
        extraBindings!!.put(variableId, value)
        return this
    }

    /**
     * 清除额外的变量绑定, 一般在 [OnItemBind.onItemBind]被调用
     */
    fun clearExtras(): ItemBinding<T> {
        if (extraBindings != null) {
            extraBindings!!.clear()
        }
        return this
    }

    /**
     * 移除额外的变量绑定, 一般在 [OnItemBind.onItemBind]被调用
     */
    fun removeExtra(variableId: Int): ItemBinding<T> {
        if (extraBindings != null) {
            extraBindings!!.remove(variableId)
        }
        return this
    }

    /**
     * 返回当前布局绑定的变量id
     */
    fun variableId(): Int {
        return variableId
    }

    /**
     *返回当前布局id
     */
    @LayoutRes
    fun layoutRes(): Int {
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
            variableId = VAR_INVALID
            layoutRes = LAYOUT_NONE
            onItemBind.onItemBind(this, position, item)
            check(variableId != VAR_INVALID) { "variableId not set in onItemBind()" }
            check(layoutRes != LAYOUT_NONE) { "layoutRes not set in onItemBind()" }
        }
    }

    /**
     *将项目和额外绑定绑定到给定的绑定。 如果有任何绑定，则返回true
     *否则为假。 这由绑定集合适配器在内部调用。
     * @throws IllegalStateException 如果该变量id不在布局当中
     */
    fun bind(binding: ViewDataBinding, item: T): Boolean {
        if (variableId == VAR_NONE) {
            return false
        }
        val result = binding.setVariable(variableId, item)

        if (defaultItemVariableId != null) {
            binding.setVariable(defaultItemVariableId!!, item)
        }

        if (clickListener != null) {
            binding.setVariable(defaultClickVariableId!!, clickListener)
        }


        if (!result) {
            Utils.throwMissingVariable(binding, variableId, layoutRes)
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
        fun <T> of(variableId: Int, @LayoutRes layoutRes: Int): ItemBinding<T> {
            return ItemBinding<T>(null).set(variableId, layoutRes)
        }

        /**
         * 使用给定的回调构造一个实例。 它将被调用为每个item布局设置绑定信息。
         *
         * @see OnItemBind
         */
        @JvmStatic
        fun <T> of(onItemBind: OnItemBind<T>): ItemBinding<T> {
            return ItemBinding(onItemBind)
        }
    }
}
