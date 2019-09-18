package me.lx.rv.loadmore;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2019/9/6 19:01
 *  version: 1.0
 *  desc: 加载更多的ViewHolder, 实现了ILoadMore接口后,在父类AbstractLoadMoreFooter去
 *  具体的实现.易于扩展
 */
class LoadMoreViewHolder extends RecyclerView.ViewHolder implements ILoadMore {

    private AbstractLoadMoreFooter mFooter;

     LoadMoreViewHolder(View itemView, AbstractLoadMoreFooter footer) {
        super(itemView);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        if (params instanceof StaggeredGridLayoutManager.LayoutParams) {
            ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
        }
        this.mFooter = footer;
        footer.onCreate(itemView);
    }

    void setState(int stateType) {
        switch (stateType) {
            case LoadMoreAdapter.STATE_LOADING:
                loading();
                break;
            case LoadMoreAdapter.STATE_LOAD_FAILED:
                loadFailed();
                break;
            case LoadMoreAdapter.STATE_NO_MORE_DATA:
                noMoreData();
                break;
        }
    }

    @Override
    public void loading() {
        mFooter.loading();
    }

    @Override
    public void noMoreData() {
        mFooter.noMoreData();
    }

    @Override
    public void loadFailed() {
        mFooter.loadFailed();
    }
}
