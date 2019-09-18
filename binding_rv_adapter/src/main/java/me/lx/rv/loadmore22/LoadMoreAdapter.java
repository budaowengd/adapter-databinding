//package me.lx.rv.loadmore22;
//
//import android.database.Observable;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.StaggeredGridLayoutManager;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//
//import me.lx.rv.loadmore.LoadMoreFooter;
//import me.lx.rv.loadmore.LoadMoreViewHolder;
//
//@SuppressWarnings({"unchecked", "WeakerAccess", "unused"})
//public class LoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    /**
//     *
//     */
//    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
//    /**
//     *
//     */
//    private int VIEW_TYPE_LOAD_MORE = 1111;
//    /**
//     *
//     */
//    private OnLoadMoreListener mOnLoadMoreListener;
//    /**
//     *
//     */
//    private OnFailedClickListener mOnFailedClickListener;
//    /**
//     * 是否为上拉
//     */
//    private boolean mIsScrollLoadMore = false;
//
//    /**
//     * footer的状态
//     */
//    static final int STATE_LOADING = 0;
////    static final int STATE_LOAD_COMPLETE = 1;
//    static final int STATE_LOAD_FAILED = 2;
//    static final int STATE_NO_MORE_DATA = 3;
//    private int mStateType = STATE_LOADING;
//
//    /**
//     * 已无更多数据
//     */
//    private boolean mNoMoreData = false;
//
//    /**
//     *
//     */
//    private RecyclerView mRecyclerView;
//    /**
//     *
//     */
//    private AbsLoadMoreFooter mFooter;
//
//    public static LoadMoreAdapter wrap(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
//        return new LoadMoreAdapter(adapter);
//    }
//
//    public static LoadMoreAdapter wrap(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, AbsLoadMoreFooter footer) {
//        return new LoadMoreAdapter(adapter, footer);
//    }
//
//    private LoadMoreAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
//        this(adapter, new LoadMoreFooter());
//    }
//
//    private LoadMoreAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, AbstractMethodError footer) {
//        if (adapter == null) {
//            throw new NullPointerException("mAdapter can not be null");
//        }
//        this.mAdapter = adapter;
//        this.mFooter = footer;
//    }
//
//    @Override
//    public int getItemCount() {
//        int count = mAdapter.getItemCount();
//        return count > 0 ? count + 1 : 0;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        if (position == 0) return mAdapter.getItemId(position);
//        if (getItemViewType(position) == VIEW_TYPE_LOAD_MORE) return VIEW_TYPE_LOAD_MORE;
//        return mAdapter.getItemId(position);
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (position <= 0) return super.getItemViewType(position);
//        if (position == getItemCount() - 1) {
//            return VIEW_TYPE_LOAD_MORE;
//        }
//        return super.getItemViewType(position);
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == VIEW_TYPE_LOAD_MORE) {
//            final View footView = LayoutInflater.from(parent.getContext())
//                    .inflate(mFooter.setLayoutRes(), parent, false);
//            mFooter.onCreate(footView);
//            return new LoadMoreViewHolder(footView, mFooter);
//        }
//        return mAdapter.onCreateViewHolder(parent, viewType);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
//        if (holder instanceof LoadMoreViewHolder) {
//            final LoadMoreViewHolder loadMoreVH = (LoadMoreViewHolder) holder;
//            //首次如果itemView没有填充满RecyclerView，继续加载更多
//            if (!mRecyclerView.canScrollVertically(-1) && mOnLoadMoreListener != null) {
//                //fix bug Cannot call this method while RecyclerView is computing a layout or scrolling
//                mRecyclerView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mStateType = STATE_LOADING;
//                        loadMoreVH.setState(mStateType);
//                        mOnLoadMoreListener.onLoadMore(LoadMoreAdapter.this);
//                    }
//                });
//            }
//
//            //加载失败点击事件
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mStateType == STATE_LOAD_FAILED && mOnFailedClickListener != null) {
//                        mStateType = STATE_LOADING;
//                        mOnFailedClickListener.onClick(LoadMoreAdapter.this, holder.itemView);
//                    }
//                }
//            });
//
//            //更新状态
//            loadMoreVH.setState(mStateType);
//            Log("stateType == " + mStateType);
//        } else {
//            mAdapter.onBindViewHolder(holder, position, payloads);
//        }
//    }
//
//    @Override
//    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
//        mRecyclerView = recyclerView;
//        setFullSpan(recyclerView);
//
//        if (!isRegistered()) {
//            mAdapter.registerAdapterDataObserver(dataObserver);
//        }
//
//        recyclerView.addOnScrollListener(mOnScrollListener);
//
//        mAdapter.onAttachedToRecyclerView(recyclerView);
//    }
//
//    @Override
//    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
//        mRecyclerView = null;
//
//        if (isRegistered()) {
//            mAdapter.unregisterAdapterDataObserver(dataObserver);
//        }
//
//        recyclerView.removeOnScrollListener(mOnScrollListener);
//
//        mAdapter.onDetachedFromRecyclerView(recyclerView);
//    }
//
//    @Override
//    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
//        if (holder instanceof LoadMoreViewHolder) return;
//        mAdapter.onViewAttachedToWindow(holder);
//    }
//
//    @Override
//    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
//        if (holder instanceof LoadMoreViewHolder) return;
//        mAdapter.onViewDetachedFromWindow(holder);
//    }
//
//    @Override
//    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
//        if (holder instanceof LoadMoreViewHolder) return;
//        mAdapter.onViewRecycled(holder);
//    }
//
//    @Override
//    public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
//        if (holder instanceof LoadMoreViewHolder) return false;
//        return mAdapter.onFailedToRecycleView(holder);
//    }
//
//    private RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
//        @Override
//        public void onChanged() {
//            LoadMoreAdapter.this.notifyDataSetChanged();
//        }
//
//        @Override
//        public void onItemRangeChanged(int positionStart, int itemCount) {
//            LoadMoreAdapter.this.notifyItemRangeChanged(positionStart, itemCount);
//        }
//
//        @Override
//        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
//            LoadMoreAdapter.this.notifyItemRangeChanged(positionStart, itemCount, payload);
//        }
//
//        @Override
//        public void onItemRangeInserted(int positionStart, int itemCount) {
//            LoadMoreAdapter.this.notifyItemRangeInserted(positionStart, itemCount);
//        }
//
//        @Override
//        public void onItemRangeRemoved(int positionStart, int itemCount) {
//            LoadMoreAdapter.this.notifyItemRangeRemoved(positionStart, itemCount);
//        }
//
//        @Override
//        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
//            LoadMoreAdapter.this.notifyItemRangeChanged(fromPosition, toPosition, itemCount);
//        }
//    };
//
//    private boolean isRegistered() {
//        boolean isRegistered = false;
//        try {
//            Class<? extends RecyclerView.Adapter> clazz = RecyclerView.Adapter.class;
//            Field field = clazz.getDeclaredField("mObservable");
//            field.setAccessible(true);
//            Observable observable = (Observable) field.get(mAdapter);
//
//            Field observersField = Observable.class.getDeclaredField("mObservers");
//            observersField.setAccessible(true);
//            ArrayList<Object> list = (ArrayList<Object>) observersField.get(observable);
//            isRegistered = list.contains(dataObserver);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isRegistered;
//    }
//
//    public interface OnLoadMoreListener {
//        void onLoadMore(LoadMoreAdapter adapter);
//    }
//
//    public interface OnFailedClickListener {
//        void clickLoadMoreFailView(LoadMoreAdapter adapter, View view);
//    }
//
//    public LoadMoreAdapter setLoadMoreListener(OnLoadMoreListener listener) {
//        this.mOnLoadMoreListener = listener;
//        return this;
//    }
//
//    public LoadMoreAdapter setOnFailedClickListener(OnFailedClickListener listener) {
//        this.mOnFailedClickListener = listener;
//        return this;
//    }
//
//    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//
//            if (newState != RecyclerView.SCROLL_STATE_IDLE ||
//                    mOnLoadMoreListener == null
//                    || mNoMoreData
//                    || !mIsScrollLoadMore) return;
//
//            if (canLoadMore(recyclerView.getLayoutManager())) {
//                setState(STATE_LOADING);
//                mOnLoadMoreListener.onLoadMore(LoadMoreAdapter.this);
//            }
//        }
//
//        @Override
//        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//            mIsScrollLoadMore = dy > 0;
//        }
//    };
//
//    private boolean canLoadMore(final RecyclerView.LayoutManager layoutManager) {
//        boolean canLoadMore = false;
//        if (layoutManager instanceof GridLayoutManager) {
//            canLoadMore = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition() >= layoutManager.getItemCount() - 1;
//        } else if (layoutManager instanceof LinearLayoutManager) {
//            canLoadMore = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition() >= layoutManager.getItemCount() - 1;
//        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//            StaggeredGridLayoutManager sgLayoutManager = ((StaggeredGridLayoutManager) layoutManager);
//            int[] into = new int[sgLayoutManager.getSpanCount()];
//            int[] lastVisibleItemPositions = sgLayoutManager.findLastVisibleItemPositions(into);
//            int lastPosition = lastVisibleItemPositions[0];
//            for (int value : into) {
//                if (value > lastPosition) {
//                    lastPosition = value;
//                }
//            }
//            canLoadMore = lastPosition >= layoutManager.getItemCount() - 1;
//        }
//        return canLoadMore;
//    }
//
//    public RecyclerView.Adapter<RecyclerView.ViewHolder> getRealAdapter() {
//        return mAdapter;
//    }
//
//
////    public void loadFailed() {
////        setState(STATE_LOAD_FAILED);
////    }
////
////    public void noMoreData() {
////        mNoMoreData = true;
////        setState(STATE_NO_MORE_DATA);
////    }
//
//    private void setState(int state) {
//        if (mStateType == state) return;
//        this.mStateType = state;
//        notifyLoadMoreVH();
//    }
//
//    public void resetNoMoreData() {
//        mNoMoreData = false;
//    }
//
//    private void notifyLoadMoreVH() {
//        if (getItemCount() <= 0) return;
//        LoadMoreAdapter.this.notifyItemChanged(getItemCount() - 1);
//    }
//
//    private void setFullSpan(RecyclerView recyclerView) {
//        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//        if (layoutManager == null) return;
//
//        if (layoutManager instanceof GridLayoutManager) {
//            final GridLayoutManager gm = (GridLayoutManager) layoutManager;
//            gm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    int viewType = getItemViewType(position);
//                    if (viewType == VIEW_TYPE_LOAD_MORE) return gm.getSpanCount();
//                    return 1;
//                }
//            });
//        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//            StaggeredGridLayoutManager sgm = (StaggeredGridLayoutManager) layoutManager;
//        }
//    }
//
//    private void Log(String text) {
//        Log.d("LoadMoreAdapter", text);
//    }
//
//
//}
