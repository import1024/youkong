package io.github.import1024.youkong.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.import1024.youkong.R;
import io.github.import1024.youkong.common.base.Constant;
import io.github.import1024.youkong.ui.adapter.NewsListAdapter;
import io.github.import1024.youkong.ui.fragment.NewsListFragment;
import io.github.import1024.youkong.common.utils.PrefUtil;

public class NewsListActivity extends AppCompatActivity {

    @Bind(R.id.fl_main)
    ViewGroup mViewGroup;
    @Bind(R.id.iv_main)
    ImageView mIvMain;

    private NewsListFragment mFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(PrefUtil.getThemeRes());
        setContentView(R.layout.activity_news_list);
        ButterKnife.bind(this);
        addFragment(0, 0, null, null);
    }



    private void addFragment(int position, int scroll, NewsListAdapter adapter, String curDate) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (mFragment != null) {
            transaction.remove(mFragment);
        }
        mFragment = NewsListFragment.newInstance(position, scroll, adapter, curDate);
        mFragment.setmOnRecyclerViewCreated(new onViewCreatedListener());
        transaction.replace(R.id.fl_container, mFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        addMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void addMenu(Menu menu) {
        SubMenu subMenu = menu.addSubMenu("");
        subMenu.add(getString(R.string.menu_my_keep))
                .setIcon(R.drawable.ic_favorite_black_24dp)
                .setOnMenuItemClickListener(item -> {
                KeepActivity.start(NewsListActivity.this);
                return false;
        });
        subMenu.add(getString(R.string.menu_theme_change))
                .setIcon(R.drawable.ic_brightness_4_black_24dp)
                .setOnMenuItemClickListener(item -> {
                boolean isNight = PrefUtil.isNight();
                if (isNight) {
                    PrefUtil.setDay();
                    setTheme(Constant.RESOURCES_DAYTHEME);
                } else {
                    PrefUtil.setNight();
                    setTheme(Constant.RESOURCES_NIGHTTHEME);
                }
                setDrawableCahe();
                getState();
                return false;
        });
        subMenu.add(getString(R.string.menu_about))
                .setIcon(R.drawable.ic_info_black_24dp)
                .setOnMenuItemClickListener(item -> {
                AboutActivity.start(NewsListActivity.this);
                return false;
        });

        MenuItem item = subMenu.getItem();
        item.setIcon(R.drawable.ic_more_vert_white_24dp);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void setDrawableCahe() {
        //设置false清除缓存
        mViewGroup.setDrawingCacheEnabled(false);
        //设置true之后可以获取Bitmap
        mViewGroup.setDrawingCacheEnabled(true);
        mIvMain.setImageBitmap(mViewGroup.getDrawingCache());
        mIvMain.setAlpha(1f);
        mIvMain.setVisibility(View.VISIBLE);
    }

    public void getState() {
        RecyclerView recyclerView = mFragment.getRecyclerView();
        recyclerView.stopScroll();
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int position = layoutManager.findFirstVisibleItemPosition();
            int scroll = recyclerView.getChildAt(0).getTop();
            addFragment(position, scroll, mFragment.getmNewsListAdapter(), mFragment.getCurDate());
        }
    }

    private void startAnimation(final View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(1f).setDuration(1000);
        animator.addUpdateListener(animation -> {
            float n = (float) animation.getAnimatedValue();
            view.setAlpha(1f - n);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIvMain.setVisibility(View.INVISIBLE);
            }
        });
        animator.start();
    }

    class onViewCreatedListener implements NewsListFragment.OnRecyclerViewCreated {
        @Override
        public void recyclerViewCreated() {
            startAnimation(mIvMain);
        }
    }
}
