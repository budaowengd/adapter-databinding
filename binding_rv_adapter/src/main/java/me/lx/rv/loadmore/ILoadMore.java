package me.lx.rv.loadmore;

public interface ILoadMore {

    /**
     * 加载更多中
     */
    void loading();

    /**
     * 加载完成-已无更多数据
     */
    void noMoreData();

    /**
     * 加载失败
     */
    void loadFailed();
}
