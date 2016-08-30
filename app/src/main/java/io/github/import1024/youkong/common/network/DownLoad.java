package io.github.import1024.youkong.common.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.import1024.youkong.common.utils.FilerHelper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by redback on 8/22/16.
 */
public class DownLoad {

    private DownLoad(){}
    private OkHttpClient mOkHttpClient = new OkHttpClient();


    private static class DownloadHolder {
        private static final DownLoad download = new DownLoad();
        private static final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);
    }

    public static final DownLoad getDownload() {
        return DownloadHolder.download;
    }

    public static final ExecutorService getPool() {
        return DownloadHolder.pool;
    }


    public void download(List<String> urls, List<String> fileNames) {
        final int uriSize = urls.size();
        for (int i = 0; i< uriSize;i++) {
            execute(getPool(), i, urls, fileNames);
        }
    }



    public void downLoad(String url, String fileName) {
        getPool().execute(()->{
            try {
                writeByPath(fileName,buildCall(url).execute().body().byteStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void execute(ExecutorService executorService, int i, final List<String> urls, final List<String> fileNames) {
        final int j = i;
        executorService.execute(() -> {
                String url = urls.get(j);
                String file = fileNames.get(j);
                try {
                    writeByPath(file,buildCall(url).execute().body().byteStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
    }

    private Call buildCall(String url) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        return mOkHttpClient.newCall(request);
    }

    private void writeByPath(String fileName, InputStream inputStream) {
        if (FilerHelper.isMounted()) {
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(inputStream);
                File file = new File(fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                bos = new BufferedOutputStream(new FileOutputStream(file));
                byte[] bytes = new byte[1024];
                int n;
                while ((n = bis.read(bytes)) != -1) {
                    bos.write(bytes, 0, n);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                    try {

                        if (bos != null) {
                            bos.flush();
                            bos.close();
                        }
                        if (bis != null) {
                            bis.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        }
    }

}
