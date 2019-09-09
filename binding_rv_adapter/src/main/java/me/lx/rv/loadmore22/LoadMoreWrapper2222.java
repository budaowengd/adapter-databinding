package me.lx.rv.loadmore22;

import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Nukc
 */
public class LoadMoreWrapper2222 {

    private final LoadMoreAdapter2222 mLoadMoreAdapter;

    public LoadMoreWrapper2222(LoadMoreAdapter2222 loadMoreAdapter) {
        mLoadMoreAdapter = loadMoreAdapter;
    }

    public static LoadMoreWrapper2222 with(RecyclerView.Adapter adapter) {
        LoadMoreAdapter2222 loadMoreAdapter = new LoadMoreAdapter2222(adapter);
        return new LoadMoreWrapper2222(loadMoreAdapter);
    }

    public LoadMoreWrapper2222 setFooterView(@LayoutRes int resId) {
        mLoadMoreAdapter.setFooterView(resId);
        return this;
    }

    public LoadMoreWrapper2222 setFooterView(View footerView) {
        mLoadMoreAdapter.setFooterView(footerView);
        return this;
    }

    public View getFooterView() {
        return mLoadMoreAdapter.getFooterView();
    }

    public LoadMoreWrapper2222 setNoMoreView(@LayoutRes int resId) {
        mLoadMoreAdapter.setNoMoreView(resId);
        return this;
    }

    public LoadMoreWrapper2222 setNoMoreView(View noMoreView) {
        mLoadMoreAdapter.setNoMoreView(noMoreView);
        return this;
    }

    public View getNoMoreView() {
        return mLoadMoreAdapter.getNoMoreView();
    }

    public LoadMoreWrapper2222 setLoadFailedView(@LayoutRes int resId) {
        mLoadMoreAdapter.setLoadFailedView(resId);
        return this;
    }

    public LoadMoreWrapper2222 setLoadFailedView(View view) {
        mLoadMoreAdapter.setLoadFailedView(view);
        return this;
    }

    public View getLoadFailedView() {
        return mLoadMoreAdapter.getLoadFailedView();
    }

    /**
     * 监听加载更多触发事件
     *
     * @param listener {@link com.github.nukc.LoadMoreWrapper2222.LoadMoreAdapter2222.OnLoadMoreListener}
     */
    public LoadMoreWrapper2222 setListener(LoadMoreAdapter2222.OnLoadMoreListener listener) {
        mLoadMoreAdapter.setLoadMoreListener(listener);
        return this;
    }

    /**
     * 设置是否启用加载更多
     *
     * @param enabled default true
     */
    public LoadMoreWrapper2222 setLoadMoreEnabled(boolean enabled) {
        mLoadMoreAdapter.setLoadMoreEnabled(enabled);
        if (!enabled) {
            mLoadMoreAdapter.setShouldRemove(true);
        }
        return this;
    }

    /**
     * 设置全部加载完后是否显示没有更多视图
     *
     * @param enabled default false
     */
    public LoadMoreWrapper2222 setShowNoMoreEnabled(boolean enabled) {
        mLoadMoreAdapter.setShowNoMoreEnabled(enabled);
        return this;
    }

    /**
     * 设置加载失败
     */
    public void setLoadFailed(boolean isLoadFailed) {
        mLoadMoreAdapter.setLoadFailed(isLoadFailed);
    }

    /**
     * 获取原来的 adapter
     */
    public RecyclerView.Adapter getOriginalAdapter() {
        return mLoadMoreAdapter.getOriginalAdapter();
    }

    public LoadMoreAdapter2222 into(RecyclerView recyclerView) {
        mLoadMoreAdapter.setHasStableIds(mLoadMoreAdapter.getOriginalAdapter().hasStableIds());
        recyclerView.setAdapter(mLoadMoreAdapter);
        return mLoadMoreAdapter;
    }
}
