package io.github.import1024.youkong.common.network.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import io.github.import1024.youkong.common.base.App;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by caofeng on 16-8-18.
 */
public class FileManager {
    private static int CACHE_DAY = 7;
    /**
     * save image from url
     * @param url the image url
     * @param path the same path
     * @return an uri represent the save image
     */
    public static Observable<Uri> saveImage(String url, String path) {
           return makeBitmapFromUrl(url)
                .map(bitmap -> {
                    Uri uri = null;
                    File image = new File(path,url.hashCode()+".png");
                    if (image.exists()){
                        uri = Uri.fromFile(image);
                        return uri;
                    }
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,out);

                    try {
                        if (out != null)
                            out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (out != null)
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return uri;
                });

    }

    /**
     * make bitmap from url
     * @param url
     * @return a bitmap from url
     */
    public static Observable<Bitmap> makeBitmapFromUrl(String url){
        return Observable.just(url)
                .map(args -> {
                    try {
                        return Glide.with(App.getContext()).load(args)
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .map(file -> {
                    try {
                        return new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .map(in ->{ Bitmap bitmap = BitmapFactory.decodeStream(in);
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return bitmap;
                });
    }



    public static String getStringFromFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        FileReader fr = null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static Observable<String> getHtml(String fileName) {
        return Observable.fromCallable(() -> getStringFromFile(fileName))
                .subscribeOn(Schedulers.io());
    }


//    public static boolean copyFile(String originPath, String originName, String target){
//        File orginFile = new File(originPath,originName);
//        File targetFile = new File(target,originName);
//        FileChannel inChannel = null;
//        FileChannel outChannel = null;
//        FileInputStream fin =  null;
//        FileOutputStream fout = null;
//        try {
//            fin = new FileInputStream(orginFile);
//            fout = new FileOutputStream(targetFile);
//            inChannel = fin.getChannel();
//            outChannel = fout.getChannel();
//            inChannel.transferTo(0,inChannel.size(),outChannel);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }finally {
//            try {
//                if (fin != null) {
//                    fin.close();
//                }
//                if (fout != null) {
//                    fout.close();
//                }
//
//                if (inChannel != null) {
//                    inChannel.close();
//                }
//
//                if (outChannel != null) {
//                    outChannel.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//        return true;
//    }

}
