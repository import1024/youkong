package io.github.import1024.youkong.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import io.github.import1024.youkong.R;
import io.github.import1024.youkong.common.base.BaseActivity;
import io.github.import1024.youkong.model.bean.Story;
import io.github.import1024.youkong.ui.fragment.KeepDetailFragment;

public class KeepDetailActivity extends BaseActivity {

    public static final String KEY_NEWS = "key_story";
    public static final String POSIION = "position";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_keep_detail;
    }

    public static void start(Context context, Story story,int position) {
        Intent intent = new Intent(context, KeepDetailActivity.class);
        intent.putExtra(KEY_NEWS, story);
        intent.putExtra(POSIION, position);
        context.startActivity(intent);
    }


    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mSwipeBackLayout.setEdgeDp(120); //设置滑动返回触发范围
        Story story = getIntent().getParcelableExtra(KEY_NEWS);
        int positon = getIntent().getIntExtra(POSIION, 0);
        showKeepDetailFragment(story,positon);
    }

    private void showKeepDetailFragment(Story story,int position) {
        Fragment fragment = KeepDetailFragment.newInstance(story,position);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_keep_container, fragment, KeepDetailFragment.TAG);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
