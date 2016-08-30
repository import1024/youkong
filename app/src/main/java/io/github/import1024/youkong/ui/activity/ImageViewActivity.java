package io.github.import1024.youkong.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import io.github.import1024.youkong.R;
import io.github.import1024.youkong.common.base.BaseActivity;
import io.github.import1024.youkong.common.network.manager.FileManager;
import io.github.import1024.youkong.ui.adapter.ImageViewPagerAdapter;
import io.github.import1024.youkong.common.utils.NetUtil;
import io.github.import1024.youkong.common.utils.ShareUtil;
import io.github.import1024.youkong.common.utils.ToastUtil;
import io.github.import1024.youkong.common.utils.Tools;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImageViewActivity extends BaseActivity {

    private static final String IMAGE = "image";
    private static final String IMAGES = "images";
    private static final String TAG = "view_image_activity";

    @Bind(R.id.viewpager_image)
    ViewPager mPager;

    @Bind(R.id.tv_image_number)
    TextView mTvNumber;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;


    private ImageViewPagerAdapter adapter;

    private String image;
    private ArrayList<String> images;

    private int currentPosition = 0 ;
    private int count = 0 ;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_image;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        image =  getIntent().getStringExtra(IMAGE);
        images = getIntent().getStringArrayListExtra(IMAGES);
        if (!image.startsWith("file") && !NetUtil.isNetworkConnected()){
            ToastUtil.toastShort(getString(R.string.image_load_error));
        }
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        count = images.size();
        calculatePosition();
        setIndicator();
        initPager();
    }

    private void setIndicator() {

        String indicator = getString(R.string.image_number);
        indicator = String.format(indicator,currentPosition+1,count);
        mTvNumber.setText(indicator);
    }

    /**
     *  calculate the current position of the image in the images
     */
    private void calculatePosition() {
        for (int i = 0 ; i < images.size() ; i++){
            if (images.get(i).equals(image)){
                currentPosition = i;
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }


    private void initPager() {
        adapter = new ImageViewPagerAdapter(getSupportFragmentManager());
        adapter.setImgs(images);
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(currentPosition);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                setIndicator();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_imgae_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_action_download:
                download();
                break;
            case R.id.menu_action_share:
                share();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public static void  startViewImageActivity(Context context , String image, ArrayList<String> images){
        Intent intent = new Intent(context, ImageViewActivity.class);
        intent.putExtra(IMAGE,image);
        intent.putStringArrayListExtra(IMAGES,images);
        context.startActivity(intent);
    }

    /**
     *  download image
     */
    public void download(){
        ToastUtil.toastShort(getString(R.string.saveing));
        FileManager.saveImage(images.get(currentPosition), Tools.getAlbumStorageDir("album"))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Subscriber<Uri>() {
                @Override
                public void onCompleted() {
                    ToastUtil.toastShort(getString(R.string.save_success));
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    ToastUtil.toastShort(getString(R.string.save_error));
                }

                @Override
                public void onNext(Uri uri) {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                }
            });
    }


    public void share(){
        if (images.get(currentPosition).startsWith("file")) {
            ShareUtil.shareImage(Uri.parse(images.get(currentPosition)),this);
        }else {
            ShareUtil.shareImage(this,images.get(currentPosition));
        }
    }

}
