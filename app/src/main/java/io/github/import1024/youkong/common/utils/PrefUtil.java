package io.github.import1024.youkong.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.import1024.youkong.common.base.App;
import io.github.import1024.youkong.common.base.Constant;

/**
 * Created by import1024 on 16/3/30.
 */
public class PrefUtil {

    private static final String PRE_NAME = "io.github.import1024.purezhihud_preferences";
    private static final String PRE_NIGHT = "night";

    private static SharedPreferences getSharedPreferences() {
        return App.getContext()
                .getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
    }

    public static void setNight(){
        getSharedPreferences().edit().putBoolean(PRE_NIGHT, true).commit();
    }

    public static void setDay(){
        getSharedPreferences().edit().putBoolean(PRE_NIGHT, false).commit();
    }

    public static void changeDayNight(){
        boolean change = !getSharedPreferences().getBoolean(PRE_NIGHT, false);
        getSharedPreferences().edit().putBoolean(PRE_NIGHT, change).commit();
    }

    public static boolean isNight(){
        return getSharedPreferences().getBoolean(PRE_NIGHT, false);
    }

    public static int getThemeRes(){
        if (!isNight()) {
            return Constant.RESOURCES_DAYTHEME;
        } else {
            return Constant.RESOURCES_NIGHTTHEME;
        }
    }
}
