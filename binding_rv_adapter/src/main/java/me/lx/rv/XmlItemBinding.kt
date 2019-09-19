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
class XmlItemBinding<T> constructor(private val mOnItemBind: OnItemBind<T>?) {

    private var mDefaultItemVariableId: Int = BR.item // 默认绑定变量id

    private var mDefaultClickVariableId: Int = BR.click

    private var mClickListener: ClickListener? = null
    @LayoutRes
    private var mLayoutRes: Int = 0 // 布局id

    private var mExtraBindings: SparseArray<Any>? = null // 额外的绑定变量字典

    /**
     * setVariableIdAndLayoutId
     * 设置变量id和布局id
     */
    fun set(@LayoutRes mLayoutRes: Int, variableId: Int = 0, mClickListener: ClickListener? = null): XmlItemBinding<T> {
        this.mLayoutRes = mLayoutRes
        this.mClickListener = mClickListener
        if (variableId == 0) {
            this.mDefaultItemVariableId = BR.item
        } else {
            this.mDefaultItemVariableId = variableId
        }
        return this
    }

    /**
     * 设置变量id和布局id
     */
    fun set(@LayoutRes mLayoutRes: Int, mClickListener: ClickListener): XmlItemBinding<T> {
        this.mLayoutRes = mLayoutRes
        this.mClickListener = mClickListener
        this.mDefaultItemVariableId = BR.item
        return this
    }

    /**
     * 设置变量id
     */
    fun setVariableId(variableId: Int): XmlItemBinding<T> {
        this.mDefaultItemVariableId = variableId
        return this
    }


    /**
     * 设置布局id
     */
    fun getLayoutRes(@LayoutRes mLayoutRes: Int): XmlItemBinding<T> {
        this.mLayoutRes = mLayoutRes
        return this
    }

    /**
     * 添加额外的变量, 绑定到视图布局.
     * 比如: 1个item需要多个变量id,可以通过该方法进行绑定
     */
    fun bindExtra(variableId: Int, value: Any): XmlItemBinding<T> {
        if (mExtraBindings == null) {
            mExtraBindings = SparseArray(1)
        }
        mExtraBindings!!.put(variableId, value)
        return this
    }

    /**
     * 清除额外的变量绑定,
     */
    fun clearExtras(): XmlItemBinding<T> {
        if (mExtraBindings != null) {
            mExtraBindings!!.clear()
        }
        return this
    }

    /**
     * 移除额外的变量绑定
     */
    fun removeExtra(variableId: Int): XmlItemBinding<T> {
        if (mExtraBindings != null) {
            mExtraBindings!!.remove(variableId)
        }
        return this
    }

    /**
     * 返回当前布局绑定的变量id
     */
    fun getDefaultVariableId(): Int {
        return mDefaultItemVariableId
    }

    /**
     *返回当前布局id
     */
    @LayoutRes
    fun getLayoutRes(): Int {
        return mLayoutRes
    }

    /**
     * 返回给定变量id的绑定对象，如果不存在，则返回null。
     */
    fun extraBinding(variableId: Int): Any? {
        return if (mExtraBindings == null) {
            null
        } else mExtraBindings!!.get(variableId)
    }

    /**
     * 如果列表只有1种item类型,mOnItemBind 会一直会为空
     * 只有当列表存在多类型的item时,mOnItemBind 才有值,
     * 更新给定项目和位置的绑定状态。 这由绑定集合适配器在内部调用.
     */
    fun xmlOnItemBind(position: Int, item: T) {
        // println("更新给定项目和位置的绑定状态。 这由绑定集合适配器在内部调用.position=$position mOnItemBind=${mOnItemBind?.hashCode()}")
        if (mOnItemBind != null) {
            mDefaultItemVariableId = VAR_INVALID
            mLayoutRes = LAYOUT_NONE
            mOnItemBind.onItemBind(this, position, item)
            check(mDefaultItemVariableId != VAR_INVALID) { "mDefaultItemVariableId not set in onItemBind()  mDefaultItemVariableId=$mDefaultItemVariableId" }
            check(mLayoutRes != LAYOUT_NONE) { "mLayoutRes not set in onItemBind()" }
        }
    }

    /**
     *将项目和额外绑定绑定到给定的绑定。 如果有任何绑定，则返回true
     *否则为假。 这由绑定集合适配器在内部调用。
     * @throws IllegalStateException 如果该变量id不在布局当中
     */
    fun bind(binding: ViewDataBinding, item: T): Boolean {
        if (mDefaultItemVariableId == VAR_NONE) {
            return false
        }
        val result = binding.setVariable(mDefaultItemVariableId, item)

        if (mClickListener != null) {
            binding.setVariable(mDefaultClickVariableId, mClickListener)
        }

        if (!result) {
            Utils.throwMissingVariable(binding, mDefaultItemVariableId, mLayoutRes)
        }
        if (mExtraBindings != null) {
            mExtraBindings!!.forEach { key, value ->
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
        fun <T> of(variableId: Int, @LayoutRes mLayoutRes: Int): XmlItemBinding<T> {
            return XmlItemBinding<T>(null).set(mLayoutRes, variableId)
        }

        /**
         *使用给定的变量id和layout构造一个实例。
         */
        fun <T> of(@LayoutRes mLayoutRes: Int, mClickListener: ClickListener): XmlItemBinding<T> {
            return XmlItemBinding<T>(null).set(mLayoutRes, mClickListener = mClickListener)
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
