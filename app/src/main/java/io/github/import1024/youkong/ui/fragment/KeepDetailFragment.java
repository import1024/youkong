package io.github.import1024.youkong.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import io.github.import1024.youkong.R;
import io.github.import1024.youkong.common.base.BaseFragment;
import io.github.import1024.youkong.model.bean.Story;
import io.github.import1024.youkong.common.network.manager.FileManager;
import io.github.import1024.youkong.ui.activity.ImageViewActivity;
import io.github.import1024.youkong.ui.activity.KeepDetailActivity;
import io.github.import1024.youkong.common.utils.L;
import io.github.import1024.youkong.common.utils.PrefUtil;
import io.github.import1024.youkong.common.utils.RxBus;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by redback on 8/26/16.
 */
public class KeepDetailFragment extends BaseFragment {
    @Bind(R.id.iv_header)
    ImageView mIvHeader;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_source)
    TextView mTvSource;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.wv_news)
    WebView mWebView;
    @Bind(R.id.tv_load_empty)
    TextView mTvLoadEmpty;
    @Bind(R.id.nested_view)
    NestedScrollView scrollView;
    Story story;
    ArrayList<String> images;
    int position;
    boolean isNight;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_keep_detail;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        story = getArguments().getParcelable(KeepDetailActivity.KEY_NEWS);
        position = getArguments().getInt(KeepDetailActivity.POSIION);
        setHasOptionsMenu(true);
        init();
        loadData();
    }

    public static Fragment newInstance(Story story, int position) {;
        Bundle bundle = new Bundle();
        bundle.putParcelable(KeepDetailActivity.KEY_NEWS, story);
        bundle.putInt(KeepDetailActivity.POSIION,position);
        Fragment fragment = new KeepDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_keep_detail,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.menu_action_delete:
                buildDislog();
                return true;
            case R.id.menu_action_share:
                share();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mCollapsingToolbarLayout.setTitleEnabled(true);
        images = new ArrayList<>();
        mToolbar.setOnClickListener(l-> scrollView.smoothScrollTo(0, 0));
    }

    private void loadData() {
                        L.object(story);
                        if (story == null) {
                            mTvLoadEmpty.setVisibility(View.VISIBLE);
                        } else {
                            Glide.with(getActivity())
                                    .load(story.image)
                                    .into(mIvHeader);
                            mTvTitle.setText(story.title);
                            mCollapsingToolbarLayout.setTitle(story.title);
                            mTvSource.setText(story.imageSource);
                            isNight = PrefUtil.isNight();
                            mWebView.setDrawingCacheEnabled(true);
                            mWebView.getSettings().setAllowContentAccess(true);
                            initWebView(mWebView);
                            if (isNight) {
                                FileManager.getHtml(story.content)
                                        .map(s -> s.replace("redback", "night"))
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(html -> mWebView.loadDataWithBaseURL("file:///android_asset/", html,"text/html", "utf-8", null));
                            } else {
                                mWebView.loadUrl("file:" + File.separator + File.separator + story.content);
                            }
                            mTvLoadEmpty.setVisibility(View.GONE);
                        }

    }



    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        mWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByClassName(\"content-image\"); " +
                "var imgs = new Array();"+
                "for(var i=0;i<objs.length;i++)  " +
                "{"+
                "    imgs[i]=objs[i].src;"+
                "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imageListener.openImage(this.src);  " +
                "    }  " +
                "}" +
                "window.imageListener.getImage(imgs); "+
                "})()");
    }



    public class JsPicInterface {


        private Context context;


        public JsPicInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void openImage(String img) {
            ImageViewActivity.startViewImageActivity(context, img, images);
        }

        @JavascriptInterface
        public void getImage(String[] imgs) {
            int length = imgs.length;
            for (int i=0;i<length;i++) {
                images.add(imgs[i]);
            }
        }

    }

    private void initWebView(WebView mWebView) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.addJavascriptInterface(new JsPicInterface(getActivity()), "imageListener");
    }



    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            addImageClickListner();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            super.onReceivedError(view, errorCode, description, failingUrl);

        }
    }

    private void buildDislog() {
        TypedValue icon = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.alert_dialog_icon, icon,true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.warning_delete_article_title)
                .setIcon(icon.resourceId)
                .setMessage(R.string.warning_delete_article_content)
                .setPositiveButton(R.string.delete, (DialogInterface dialog, int which) -> {
                    RxBus.getDefault().post(position);
                    getActivity().finish();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) ->
                        dialog.dismiss())
                .setCancelable(true)
                .show();
    }


    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_from) + story.title + "，http://daily.zhihu.com/story/" + story.id);
        startActivity(Intent.createChooser(intent, story.title));
    }

}
