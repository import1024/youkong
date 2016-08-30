package io.github.import1024.youkong.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import io.github.import1024.youkong.R;
import io.github.import1024.youkong.common.base.BaseFragment;
import io.github.import1024.youkong.model.bean.NewsList;
import io.github.import1024.youkong.model.interfaces.NewsListInterface;
import io.github.import1024.youkong.presenter.NewsListPresenter;
import io.github.import1024.youkong.ui.adapter.AutoLoadOnScrollListener;
import io.github.import1024.youkong.ui.adapter.NewsListAdapter;
import io.github.import1024.youkong.common.utils.L;

/**
 * 显示知乎日报列表
 * Created by import1024 on 16/3/16.
 */
public class NewsListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, NewsListInterface{

    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_SCROLL = "scroll";
    public static final String EXTRA_CURDATE = "currentDate";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tv_load_empty)
    TextView mTvLoadEmpty;
    @Bind(R.id.tv_load_error)
    TextView mTvLoadError;
    @Bind(R.id.rcv_news_list)
    RecyclerView mRcvNewsList;
    @Bind(R.id.ptr_news_list)
    SwipeRefreshLayout mPtrNewsList;

    public NewsListAdapter mExtraAdapter;
    private NewsListAdapter mNewsListAdapter;
    private String curDate;
    private AutoLoadOnScrollListener mAutoLoadListener;
    private Snackbar mLoadLatestSnackbar;
    private Snackbar mLoadBeforeSnackbar;
    private OnRecyclerViewCreated mOnRecyclerViewCreated;
    private LinearLayoutManager mLinearLayoutManager;
    private NewsListPresenter present;

    private boolean move = false;
    //记录顶部显示的项
    private int position = 0;
    //记录顶部项的偏移
    private int scroll = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_list;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        init();
        if (mNewsListAdapter.getmNewsList().size() == 0) {
            present.loadLatestNews();
        }
    }

    public static NewsListFragment newInstance(int position, int scroll, NewsListAdapter adapter, String curDate) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_POSITION, position);
        bundle.putInt(EXTRA_SCROLL, scroll);
        bundle.putString(EXTRA_CURDATE,curDate);
        NewsListFragment fragment = new NewsListFragment();
        fragment.setArguments(bundle);
        fragment.mExtraAdapter = adapter;
        return fragment;
    }

    private void init() {
        present = new NewsListPresenter(getActivity(),this);
        position = getArguments().getInt(EXTRA_POSITION);
        scroll = getArguments().getInt(EXTRA_SCROLL);
        curDate = getArguments().getString(EXTRA_CURDATE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        mPtrNewsList.setOnRefreshListener(this);
        mPtrNewsList.setColorSchemeColors(Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.RED);

        //配置RecyclerView
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRcvNewsList.setLayoutManager(mLinearLayoutManager);
        mRcvNewsList.setHasFixedSize(true);
        mRcvNewsList.setItemAnimator(new DefaultItemAnimator());
        mNewsListAdapter = new NewsListAdapter(getActivity(), new ArrayList<>());
        mRcvNewsList.setAdapter(mNewsListAdapter);
        mAutoLoadListener = new AutoLoadOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                present.loadBeforeNews(curDate);
            }
        };
        mRcvNewsList.addOnScrollListener(mAutoLoadListener);
        mRcvNewsList.addOnScrollListener(new RecyclerViewListener());
        if (mExtraAdapter != null) {
            mNewsListAdapter.setAnim(false);
            mNewsListAdapter.setmNewsList(mExtraAdapter.getmNewsList());
            mNewsListAdapter.notifyDataSetChanged();
            move();
        }
        mToolbar.setOnClickListener(l -> mRcvNewsList.smoothScrollToPosition(0));
        mLoadLatestSnackbar = Snackbar.make(mRcvNewsList, R.string.load_fail, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.refresh, v -> present.loadLatestNews());
        mLoadBeforeSnackbar = Snackbar.make(mRcvNewsList, R.string.load_more_fail, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.refresh, v -> present.loadBeforeNews(curDate));
    }

    private void move() {
        if (position < 0 || position >= mRcvNewsList.getAdapter().getItemCount()) {
            return;
        }
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        if (position <= firstItem) {
            mRcvNewsList.scrollToPosition(position);
            move = true;
        } else if (position <= lastItem) {
            int top = mRcvNewsList.getChildAt(position - firstItem).getTop() - scroll;
            mRcvNewsList.scrollBy(0, top);
            if (mOnRecyclerViewCreated != null) {
                mOnRecyclerViewCreated.recyclerViewCreated();
            }
        } else {
            mRcvNewsList.scrollToPosition(position);
            move = true;
        }
    }

    @Override
    public void refresh(boolean flag) {
        mPtrNewsList.setRefreshing(flag);
    }

    @Override
    public void loadBeforeNewsError() {
        mAutoLoadListener.setLoading(false);
        mLoadBeforeSnackbar.show();
    }

    @Override
    public void loadBeforeNewsSuccess(NewsList newsList) {
        mAutoLoadListener.setLoading(false);
        mLoadBeforeSnackbar.dismiss();
        mNewsListAdapter.addData(newsList.getStories());
        curDate = newsList.getDate();
        L.object(newsList.getStories());
    }

    @Override
    public void loadLatestNewsError() {
        mPtrNewsList.setRefreshing(false);
        mLoadLatestSnackbar.show();
        mTvLoadError.setVisibility(View.VISIBLE);
        mTvLoadEmpty.setVisibility(View.GONE);
    }

    @Override
    public void loadLatestNewsSuccess(NewsList newsList) {
        mPtrNewsList.setRefreshing(false);
        L.object(newsList.getStories());
        if (newsList.getStories() == null) {
            mTvLoadEmpty.setVisibility(View.VISIBLE);
        } else {
            mNewsListAdapter.changeData(newsList.getStories());
            curDate = newsList.getDate();
            mTvLoadEmpty.setVisibility(View.GONE);
        }
        mTvLoadError.setVisibility(View.GONE);
        mLoadLatestSnackbar.dismiss();
        if (newsList.getStories().size() < 8) {
            present.loadBeforeNews(curDate);
        }
    }

    class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (move) {
                move = false;
                int n = position - mLinearLayoutManager.findFirstVisibleItemPosition();
                if (0 <= n && n < mRcvNewsList.getChildCount()) {
                    int top = mRcvNewsList.getChildAt(n).getTop() - scroll;
                    mRcvNewsList.smoothScrollBy(0, top);
                }
                if (mOnRecyclerViewCreated != null) {
                    mOnRecyclerViewCreated.recyclerViewCreated();
                }
            }
        }
    }


    public void setmOnRecyclerViewCreated(OnRecyclerViewCreated mOnRecyclerViewCreated) {
        this.mOnRecyclerViewCreated = mOnRecyclerViewCreated;
    }

    public String getCurDate(){
        return curDate;
    }


    public NewsListAdapter getmNewsListAdapter() {
        return mNewsListAdapter;
    }

    @Override
    public void onRefresh() {
        present.loadLatestNews();
    }


    public RecyclerView getRecyclerView() {
        return mRcvNewsList;
    }

    public interface OnRecyclerViewCreated {
        void recyclerViewCreated();
    }

}
