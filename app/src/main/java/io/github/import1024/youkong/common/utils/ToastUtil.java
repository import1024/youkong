package io.github.import1024.youkong.common.utils;

import android.widget.Toast;

import io.github.import1024.youkong.common.base.App;

/**
 * Created by caofeng on 16-8-19.
 */
public class ToastUtil {
    public static void toastShort(String msg){
        Toast.makeText(App.getContext(),msg, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String msg){
        Toast.makeText(App.getContext(),msg, Toast.LENGTH_LONG).show();
    }
}
