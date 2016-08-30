package io.github.import1024.youkong.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import io.github.import1024.youkong.R;
import io.github.import1024.youkong.common.base.BaseActivity;
import io.github.import1024.youkong.model.bean.Story;
import io.github.import1024.youkong.presenter.KeepPresenter;
import io.github.import1024.youkong.common.utils.PrefUtil;
import io.github.import1024.youkong.common.utils.RxBus;
import io.github.import1024.youkong.common.utils.ToastUtil;
import io.github.import1024.youkong.common.utils.Tools;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class KeepActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tv_load_empty)
    TextView mTvLoadEmpty;
    @Bind(R.id.rcv_news_list)
    RecyclerView mStoriesList;
    List<Story> stories;
    KeepPresenter presenter;
    CommonAdapter adapter;
    private boolean isNight = PrefUtil.isNight();

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mSwipeBackLayout.setEdgeDp(100); //设置滑动返回触发范围
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setOnClickListener(l -> mStoriesList.smoothScrollToPosition(0));
        mStoriesList.setLayoutManager(new LinearLayoutManager(this));
        mStoriesList.setHasFixedSize(true);
        presenter = new KeepPresenter(this);
        presenter.getAllStories().subscribe(s -> {
            stories = s;
            if (s == null || s.size() == 0) {
                mTvLoadEmpty.setVisibility(View.VISIBLE);
                mTvLoadEmpty.setText(R.string.keep_no_save);
            } else {
                mTvLoadEmpty.setVisibility(View.GONE);
                adapter = new CommonAdapter<Story>(this, R.layout.item_news_list, stories) {
                    @Override
                    protected void convert(ViewHolder holder, Story story, int position) {
                        TextView tv = holder.getView(R.id.tv_title);
                        if (!isNight) {
                            tv.setTextColor(ContextCompat.getColor(mContext, R.color.textColorFirst_Day));
                        } else {
                            tv.setTextColor(ContextCompat.getColor(mContext, R.color.textColorFirst_Night));
                        }
                        tv.setText(story.title);
                        ImageView imageView =  holder.getView(R.id.iv_news);
                        Glide.with(KeepActivity.this).load(story.image).placeholder(R.drawable.ic_placeholder).into(imageView);
                    }
                };
                adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                        KeepDetailActivity.start(KeepActivity.this, stories.get(position),position);
                    }
                    @Override
                    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                        return false;
                    }
                });
                mStoriesList.setAdapter(adapter);
            }
        });
        RxBus.getDefault().toObserverable(Integer.class).subscribe(i ->{
            int position = i;
            presenter.deleteStory(stories.get(position).id);
            stories.remove(position);
            adapter.notifyItemRemoved(position);
        });

        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int deletePosition = viewHolder.getLayoutPosition();
                        Observable.just(stories.get(deletePosition).id)
                                .doOnNext(id -> presenter.deleteStory2(id))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Subscriber<Integer>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Integer integer) {
                                        stories.remove(deletePosition);
                                        adapter.notifyItemRemoved(deletePosition);
                                    }
                                });
                    }
                }).attachToRecyclerView(mStoriesList);

    }

    public static void start(Context context) {
        Intent intent = new Intent(context, KeepActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_keep, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_delete_all:
                buildDislog();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void buildDislog() {
        TypedValue icon = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.alert_dialog_icon, icon,true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_delete_all_title)
                .setIcon(icon.resourceId)
                .setMessage(R.string.warning_delete_all_content)
                .setPositiveButton(R.string.delete, (DialogInterface dialog, int which) -> {
                    deleteAll();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) ->
                        dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void deleteAll() {
        presenter.deleteAll().doOnNext(s->{ if (s) {
            String root = Tools.getRootPath(KeepActivity.this);
            if (root != null) {
                File file = new File(root);
                if (file.exists() && file.isDirectory()) {
                    Tools.deleteDir(file);
                }
            }
        }}).observeOn(AndroidSchedulers.mainThread()).subscribe(t -> {
            if (t && stories!= null && !stories.isEmpty()) {
                stories.clear();
                adapter.notifyDataSetChanged();
                mTvLoadEmpty.setVisibility(View.VISIBLE);
                ToastUtil.toastShort(getString(R.string.remove_all_article));
            }
        });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_keep;
    }

}
