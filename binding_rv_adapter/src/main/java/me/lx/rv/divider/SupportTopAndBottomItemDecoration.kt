package me.lx.rv.divider

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView


/**
 * @author: luoXiong
 * @date: 2019/1/3 18:13
 * @version: 1.0
 * @desc: 垂直列表的分割线
 * androidx.recyclerview.widget.DividerItemDecoration
 */
class SupportTopAndBottomItemDecoration : RecyclerView.ItemDecoration {
    private var mDivider: Drawable? = null
    private val mBounds = Rect()
    private var isSupportTopHaveDivider = false // 最顶部是否支持分割线
    private var isSupportBottomHaveDivider = true // 最底部是否支持分割线

    companion object{
       public val ATTRS = intArrayOf(android.R.attr.listDivider)
    }

    constructor(context: Context) {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    constructor(context: Context, @ColorInt itemDividerColor: Int, pxHeight: Int? = null) {
        setDividerColor(context, itemDividerColor, pxHeight)
    }


    fun setSupportTopHaveDivider(haveDivider: Boolean): SupportTopAndBottomItemDecoration {
        isSupportTopHaveDivider = haveDivider
        return this
    }

    fun setSupportBottomHaveDivider(haveDivider: Boolean): SupportTopAndBottomItemDecoration {
        isSupportBottomHaveDivider = haveDivider
        return this
    }

    /**
     * Sets the [Drawable] for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    fun setDrawable(drawable: Drawable) {
        mDivider = drawable
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || mDivider == null) {
            return
        }
        drawVertical(c, parent)
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int

        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            if (i == 0) {
                if (isSupportTopHaveDivider) {
                    mDivider!!.setBounds(left, 0, right, mDivider!!.intrinsicHeight)
                    mDivider!!.draw(canvas)
                }
            }

            if (i == childCount - 1 && !isSupportBottomHaveDivider) {
                continue
            }

            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + Math.round(child.translationY)
            val top = bottom - mDivider!!.intrinsicHeight
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(canvas)
        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int

        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(parent.paddingLeft, top, parent.width - parent.paddingRight, bottom)
        } else {
            top = 0
            bottom = parent.height
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
            val right = mBounds.right + Math.round(child.translationX)
            val left = right - mDivider!!.intrinsicWidth
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0)
            return
        }
        val childPosition = parent.getChildAdapterPosition(view)
        if (childPosition == 0 && isSupportTopHaveDivider) {
            //item的第0个的顶部也支持分割线
            outRect.set(0, mDivider!!.intrinsicHeight, 0, mDivider!!.intrinsicHeight)
        } else {
            outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
        }
    }

    fun setDividerColor(context: Context, @ColorInt itemDividerColor: Int, pxHeight: Int? = null): SupportTopAndBottomItemDecoration {
        val gd = GradientDrawable()
        gd.setColor(itemDividerColor)
        var height = pxHeight
        if (height == null || height == 0) {
            height = Resources.getSystem().displayMetrics.density.toInt()
        }
        gd.setSize(0, height)
        mDivider = gd
        return this
    }
}
