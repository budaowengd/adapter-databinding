package me.lx.sample.divider;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * author: luoxiong
 * date :  2018/10/8
 * desc ：3列的分割线抽象类，必须满足以下条件才能使用
 * 1、最左和最右的间距必须是一样的
 * 2、中间两侧间距是一样的
 * 如：8 12 12 8
 * 如：10 16 16 10
 * <p>
 * 3列1：   7 17 17 7，默认16平分
 * 0左=7
 * 1=左8
 * 2=左9
 * <p>
 * 3列2：  6 15 15 6，默认14平分
 * 0左=6，
 * 1左=7，
 * 2左=8，
 * <p>
 * 规律：
 * 0左=r
 * 1左= m+ r - 平分
 * 2左=平分-r
 */
public abstract class BaseThreeGridItemDecoration extends RecyclerView.ItemDecoration {


    private int mSpanCount;  //列表显示的列数
    private boolean haveHeadLayout = false;

    private int LeftRightGap = 0; //左右间隙
    private int middleGap = 0; //中间间隙
    private int avgGap = 0;  //4个间距之间的平分距离
    private int mOneLeft;//第1列离左边的间距
    private int mTwoLeft;
    private final GridLayoutManager mGm;

    public BaseThreeGridItemDecoration(GridLayoutManager gm) {
        mGm = gm;

        mSpanCount = gm.getSpanCount();
        LeftRightGap = RvDpUtils.Companion.dp2px(getLeftRightGap());
        middleGap = RvDpUtils.Companion.dp2px(getMiddleGap());
        haveHeadLayout = isHaveHeadLayout();

        avgGap = (LeftRightGap * 2 + middleGap * 2) / 3;

        mOneLeft = middleGap + LeftRightGap - avgGap;
        mTwoLeft = avgGap - LeftRightGap;


        //Ls.d("分割线构造方法。。。666。。。avgGap="+avgGap
        //        +"  LeftRightGap="+LeftRightGap+"  middleGap="+middleGap+"  dp10="+UIUtils.dp2px(10)
        //+"  twoLeft="+twoLeft+"  threeLeft="+threeLeft);

    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int spanSize = mGm.getSpanSizeLookup().getSpanSize(position);
        // Ls.d("设置分割线了。。。spanSize=" + spanSize + "  spanCount=" + mSpanCount + " pos=" + position);
        if (mSpanCount == spanSize) {
            return;
        }
        if (haveHeadLayout) {
            position = position - 1;
        }

        //左侧
        if (position % 3 == 0) {
            outRect.set(LeftRightGap, 0, 0, 0);
        } else if (position % 3 == 1) {
            //中间
            outRect.set(mOneLeft, 0, 0, 0);
        } else {
            //右侧
            outRect.set(mTwoLeft, 0, 0, 0);
        }
    }

    /**
     * 是否有头部
     */
    public abstract boolean isHaveHeadLayout();

    /**
     * 左右间隙
     */
    public abstract int getLeftRightGap();

    /**
     * 中间间隙
     */
    public abstract int getMiddleGap();

}