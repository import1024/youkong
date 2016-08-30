package io.github.import1024.youkong.common.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.import1024.youkong.common.base.Constant;

/**
 * Created by redback on 8/22/16.
 */
public class Tools {
    public static String getData() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new Date());
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 获取收藏的根目录
     * @return 返回收藏文章根目录
     */
    public  static String getPath(Context context,int id) {
        String root = getRootPath(context);
        if (root != null) {
            File file = new File(root, id + File.separator);
            if (!file.mkdirs()) {
                Log.d("test","file is mkdirs ------------->"+file.getPath());
            }
            return file.getPath();
        }
        return null;
    }

    public static String getRootPath(Context context) {
        if (Tools.isExternalStorageWritable()) {
            File file = new File(context.getExternalFilesDir(null), Constant.SAVE_PATH + File.separator);
            if (!file.mkdirs()) {
                Log.d("test","file is mkdirs ------------->"+file.getPath());
            }
            return file.getPath();
        }
        return null;
    }

    /**
     *
     * @return 获取头部图片保存路径目录
     */
    public static String getHeadImagePath(Context context,int id) {
        String rootPath = getPath(context,id);
        if (rootPath != null) {
            File file = new File(rootPath, Constant.SAVE_HEAD_PATH);
            if (!file.mkdirs()) {
                Log.d("test","file is mkdirs ------------->"+file.getPath());
            }
            return file.getPath();
        }
        return null;
    }

    /**
     *
     * @return 获取 html 中保存图片的
     */
    public static String getNewsImagePath(Context context, int id) {
        String rootPath = getPath(context,id);
        if (rootPath != null) {
            File file = new File(rootPath, Constant.SAVE_IMAGE_PATH);
            if (!file.mkdirs()) {
                Log.e("file", "Directory not created");
            }
            return file.getPath();
        }
        return null;
    }

    public static  String getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("file", "Directory not created");
        }
        return file.getPath();
    }

    public  static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
          //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

}
