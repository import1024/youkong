package io.github.import1024.youkong.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

import io.github.import1024.youkong.R;
import io.github.import1024.youkong.common.network.manager.FileManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by caofeng on 16-8-18.
 */
public class ShareUtil {
    public static final String TAG = "share_util";

    /**
     * share image
     * @param context the context to start intent
     * @param url share image url
     */
    public static void shareImage(Context context, String url){
        Uri shareUri = null;
        String dir = Tools.getAlbumStorageDir("album");
        File shareImage = new File(dir,url.hashCode()+".png");
        if (shareImage.exists()) {
            shareUri = Uri.fromFile(shareImage);
            shareImage(shareUri,context);
        } else {
            ToastUtil.toastShort(context.getString(R.string.shareing));
            FileManager.saveImage(url,dir)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Uri>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Uri uri) {
                            shareImage(uri,context);
                        }
                    });
        }
    }

    public static void shareImage(Uri shareUri, Context context) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,shareUri);
        context. startActivity(intent);
    }

    public static void  shareStory(String url , Context context, String title){
        if (TextUtils.isEmpty(url)) {
            ToastUtil.toastShort(context.getString(R.string.no_share_content));
            return;
        }
        Intent intent = new Intent();
        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.share_from));
        builder.append(url);
        builder.append(" ( ");
        builder.append(title);
        builder.append(") ");

        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,builder.toString());
        intent.setType("text/plain");
        context.startActivity(intent);
    }
}
