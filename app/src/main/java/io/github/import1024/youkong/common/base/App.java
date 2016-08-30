package io.github.import1024.youkong.common.base;

import android.app.Application;
import android.content.Context;

import io.github.import1024.youkong.common.utils.AppContextUtil;
import io.github.import1024.youkong.common.utils.L;

/**
 * application
 * Created by import1024 on 16/3/17.
 */
public class App extends Application {

    private static Context mApplicationContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = this;
        AppContextUtil.init(this);
        L.init();
    }

    // 获取ApplicationContext
    public static Context getContext() {
        return mApplicationContext;
    }


}
