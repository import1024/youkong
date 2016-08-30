package io.github.import1024.youkong.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.github.import1024.youkong.R;
import io.github.import1024.youkong.common.base.BaseFragment;
import io.github.import1024.youkong.common.base.Constant;
import io.github.import1024.youkong.common.utils.L;
import io.github.import1024.youkong.common.utils.NetUtil;
import io.github.import1024.youkong.common.utils.PrefUtil;
import io.github.import1024.youkong.common.utils.ToastUtil;
import io.github.import1024.youkong.common.utils.Tools;
import io.github.import1024.youkong.model.bean.News;
import io.github.import1024.youkong.model.bean.NewsDetail;
import io.github.import1024.youkong.model.bean.Story;
import io.github.import1024.youkong.model.interfaces.NewsDetailInterface;
import io.github.import1024.youkong.presenter.NewsDetailPresenter;
import io.github.import1024.youkong.ui.activity.ImageViewActivity;
import io.github.import1024.youkong.ui.activity.NewsDetailActivity;

/**
 * 显示具体的知乎日报
 * Created by import1024 on 16/3/17.
 */
public class NewsDetailFragment extends BaseFragment implements NewsDetailInterface{

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
    @Bind(R.id.tv_load_error)
    TextView mTvLoadError;
    @Bind(R.id.pb_loading)
    ContentLoadingProgressBar mPbLoading;
    @Bind(R.id.nested_view)
    NestedScrollView scrollView;

    int pointX = 0;
    int pointY = 0;

    News mNews;
    ArrayList<String> images;
    String html;
    NewsDetail mNewsDetail;

    String rootPath = null;
    String headImagePath = null;
    String newsImagePath = null;
    String imageUriPre = null;
    Context context;
    int id;
    MenuItem menuItem;
    String htmlPath = null;
    int isSaved = 0;
    boolean isFinish = false;
    NewsDetailPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_detail;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        init();
        showProgress();
        presenter.loadData(id);
    }



    public static Fragment newInstance(News news) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(NewsDetailActivity.KEY_NEWS, news);
        Fragment fragment = new NewsDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail,menu);
        menuItem = menu.findItem(R.id.menu_action_favorite);
        if (isSaved != 0) {
            if (isSaved == 1) {
                menuItem.setIcon(R.drawable.ic_favorite_white_24px);
            } else {
                menuItem.setIcon(R.drawable.ic_favorite_border_white_24px);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.menu_action_share:
                presenter.share(mNews);
                return true;
            case R.id.menu_action_favorite:
                Keep();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Keep() {
        if (isFinish) {
            if (rootPath == null) {
                initPath();
            }
            if (isSaved == 1) {
                menuItem.setIcon(R.drawable.ic_favorite_border_white_24px);
                isFinish = false;
                ToastUtil.toastShort(getString(R.string.remove_article));
                presenter.deleteStory(mNewsDetail.getId())
                        .subscribe(s -> {
                    if (s) {
                        File file = new File(rootPath);
                        if (file.exists() && file.isDirectory()) {
                            Tools.deleteDir(file);
                        }
                        isSaved = 2;
                        isFinish = true;
                    }
                });
            } else {
                menuItem.setIcon(R.drawable.ic_favorite_white_24px);
                isFinish = false;
                ToastUtil.toastShort(getString(R.string.save_article));
                presenter.saveHead(mNewsDetail, headImagePath);
                presenter.insertStory(new Story(mNewsDetail.getId(), mNewsDetail.getTitle(),
                        headImagePath + mNewsDetail.getImage().hashCode() + ".png", mNewsDetail.getImage_source(), htmlPath))
                        .subscribe(s -> {
                            if (s) {
                                presenter.SaveStory(html,htmlPath,newsImagePath,imageUriPre,images,id);
                                isSaved = 1;
                                isFinish = true;
                            }
                        });
            }
        }
    }

    private void init() {
        mNews = getArguments().getParcelable(NewsDetailActivity.KEY_NEWS);
        setHasOptionsMenu(true);
        id = mNews.getId();
        context = getActivity();
        presenter = new NewsDetailPresenter(context,this);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mCollapsingToolbarLayout.setTitleEnabled(true);
        images = new ArrayList<>();
        presenter.query(mNews.getId()).subscribe(s -> {
            isSaved = s ? 1 : 2;
            if (menuItem != null) {
                menuItem.setIcon(s ? R.drawable.ic_favorite_white_24px : R.drawable.ic_favorite_border_white_24px);
            }
        });
    }


    private void initPath() {
        rootPath = Tools.getPath(context, mNews.getId()) + File.separator;
        headImagePath = Tools.getHeadImagePath(context, mNews.getId()) + File.separator;
        newsImagePath = Tools.getNewsImagePath(context, mNews.getId()) + File.separator;
        htmlPath = rootPath + mNews.getId() + ".html";
        imageUriPre = "file:" + File.separator + File.separator + newsImagePath;
    }



    private void initWebView(WebView mWebView) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.addJavascriptInterface(new JsPicInterface(getActivity()), "imageListener");
        mWebView.setOnLongClickListener(v -> {
            WebView.HitTestResult result = ((WebView) v).getHitTestResult();
            if (null == result)
                return false;

            int type = result.getType();
            if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                return false;

            if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) {
                return true;
            }

            if (WebView.HitTestResult.IMAGE_TYPE == type) {
                String src = result.getExtra();
                initPopWindow(src);
            }

            return true;});
        mWebView.setOnTouchListener((View v, MotionEvent event) -> {
                pointX = (int) event.getX();
                pointY = (int) event.getRawY();
                return false;
        });
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

    @Override
    public void loadDataSuccess(NewsDetail newsDetail) {
        hideProgress();
        L.object(newsDetail);
        if (newsDetail == null) {
            mTvLoadEmpty.setVisibility(View.VISIBLE);
        } else {
            Glide.with(getActivity())
                    .load(newsDetail.getImage())
                    .into(mIvHeader);
            mTvTitle.setText(newsDetail.getTitle());
            mCollapsingToolbarLayout.setTitle(newsDetail.getTitle());
            mTvSource.setText(newsDetail.getImage_source());
            mNewsDetail = newsDetail;
            boolean isNight = PrefUtil.isNight();

            html = presenter.getBody(newsDetail,false);
            mWebView.setDrawingCacheEnabled(true);
            mWebView.getSettings().setAllowContentAccess(true);
            initWebView(mWebView);
            String stringBuilder = presenter.getBody(newsDetail,isNight);
            mWebView.loadDataWithBaseURL("file:///android_asset/", stringBuilder, "text/html", "utf-8", null);
            mToolbar.setOnClickListener(l-> scrollView.smoothScrollTo(0, 0));
            mTvLoadEmpty.setVisibility(View.GONE);
        }
        mTvLoadError.setVisibility(View.GONE);
    }

    @Override
    public void loadDataError(Throwable throwable) {
        hideProgress();
        L.e(throwable,"Load news detail error");
        mTvLoadError.setVisibility(View.VISIBLE);
        mTvLoadEmpty.setVisibility(View.GONE);
    }


    // js通信接口

    public class JsPicInterface {

        private Context context;


        public JsPicInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void openImage(String img) {
            ImageViewActivity.startViewImageActivity(context,img,images);
        }

        @JavascriptInterface
        public void getImage(String[] imgs) {
            int length = imgs.length;
            for (int i=0;i<length;i++) {
                images.add(imgs[i]);
            }
            isFinish = true;
        }
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




    public void showProgress() {
        mPbLoading.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mPbLoading.setVisibility(View.GONE);
    }



    /**
     * 长按图片后的弹窗
     * @param src 图片链接
     */
    private void initPopWindow(String src) {
        Context context = getActivity();
        View view = LayoutInflater.from(context).inflate(R.layout.popcardview, null, false);
        TextView lookUp = (TextView) view.findViewById(R.id.item_longclicked_viewimage);
        TextView saveImage = (TextView) view.findViewById(R.id.item_longclicked_saveImage);
        TextView saveAll = (TextView) view.findViewById(R.id.item_longclicked_viewall);

        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        lookUp.setOnClickListener(l -> {
            if (!NetUtil.isNetworkConnected()) {
                ToastUtil.toastShort(getString(R.string.image_load_error));
            } else {
                ImageViewActivity.startViewImageActivity(context,src,images);
            }
            popWindow.dismiss();
        });
        saveImage.setOnClickListener(l -> {
            if (!NetUtil.isNetworkConnected()) {
                ToastUtil.toastShort(getString(R.string.image_load_error));
            } else {
                String url = Tools.getAlbumStorageDir(Constant.IMAGE_SAVE_PATH) + File.separator + mNews.getId() + src.substring(41);
                presenter.downLoad(src, url);
                ToastUtil.toastShort(getString(R.string.image_save));
            }
            popWindow.dismiss();
        });

        saveAll.setOnClickListener(l ->{
        if (!NetUtil.isNetworkConnected()) {
            ToastUtil.toastShort(getString(R.string.image_load_error));
        } else {
            List<String> fileNames = new ArrayList<>();
            String rootPath = Tools.getAlbumStorageDir(Constant.IMAGE_SAVE_PATH);
            for (String str : images) {
                String url = rootPath + File.separator + mNews.getId() + str.substring(41);
                fileNames.add(url);
            }
            presenter.downLoad(images, fileNames);
            ToastUtil.toastShort(getString(R.string.save_all_image));
        }
            popWindow.dismiss();
        });

        popWindow.setTouchable(true);

        // 这里如果返回true的话，touch事件将被拦截
        // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
        popWindow.setTouchInterceptor((v, event)-> false);
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效
        popWindow.setOnDismissListener(()->{});

        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAtLocation(mRootView, Gravity.NO_GRAVITY, pointX, pointY);
    }
}
