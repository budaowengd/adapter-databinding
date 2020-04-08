package me.lx.rv.tools;

import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class WrapperUtils {
    public interface SpanSizeCallback {
        int getWrapSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position);
    }

    public static void onAttachedToRecyclerView(RecyclerView.Adapter innerAdapter, RecyclerView recyclerView, final SpanSizeCallback callback) {
        innerAdapter.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layout = recyclerView.getLayoutManager();
        if (layout instanceof GridLayoutManager) {
            final GridLayoutManager gridLayout = (GridLayoutManager) layout;
            final GridLayoutManager.SpanSizeLookup oldLookup = gridLayout.getSpanSizeLookup();
            gridLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return callback.getWrapSpanSize(gridLayout, oldLookup, position);
                }
            });
            gridLayout.setSpanCount(gridLayout.getSpanCount());
        }
    }

    /**
     * 在这个回调里调用
     * public void onViewAttachedToWindow(RecyclerView.ViewHolder holder)
     * {
     * mInnerAdapter.onViewAttachedToWindow(holder);
     * int position = holder.getLayoutPosition();
     * if (isHeaderViewPos(position) || isFooterViewPos(position))
     * {
     * WrapperUtils.setFullSpan(holder);
     * }
     * }
     */
    public static void setFullSpan(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }
}
