package io.github.import1024.youkong.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import io.github.import1024.youkong.R;
import io.github.import1024.youkong.model.bean.News;
import io.github.import1024.youkong.model.bean.NewsDetail;
import io.github.import1024.youkong.model.bean.Story;
import io.github.import1024.youkong.model.db.dao.StoryDBImp;
import io.github.import1024.youkong.model.db.dao.StoryDBInterface;
import io.github.import1024.youkong.model.interfaces.NewsDetailInterface;
import io.github.import1024.youkong.common.network.manager.FileManager;
import io.github.import1024.youkong.common.network.manager.RetrofitManager;
import io.github.import1024.youkong.common.network.DownLoad;
import io.github.import1024.youkong.common.utils.FilerHelper;
import io.github.import1024.youkong.common.utils.NetUtil;
import io.github.import1024.youkong.common.utils.ToastUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * presenter for NewsDetailFragment
 * Created by redback on 8/29/16.
 */
public class NewsDetailPresenter {
    Context context;
    StoryDBInterface dbInterface;
    NewsDetailInterface newsDetailInterface;

    public NewsDetailPresenter(Context context, NewsDetailInterface newsDetailInterface) {
        this.context = context;
        dbInterface = new StoryDBImp(context);
        this.newsDetailInterface = newsDetailInterface;
    }


    public Observable<Boolean> deleteStory(int id) {
        return dbInterface.deleteStory(id);
    }

    public Observable<Boolean> insertStory(Story story) {
        return dbInterface.insertStory(story);
    }

    public Observable<Boolean> query(int id) {
        return dbInterface.query(id);
    }

    public void loadData(int id) {
        RetrofitManager.builder().getNewsDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsDetail -> {
                            newsDetailInterface.loadDataSuccess(newsDetail);
                        }
                        , throwable -> {
                          newsDetailInterface.loadDataError(throwable);
                        });
    }


    /**
     * 批量替换 html 中的图片链接地址
     * @param html 原 html
     * @param imgs 原图片链接地址集合
     * @return 替换后的 html
     */
    public String  ReplaceImage(String html, String newsImagePath, String imageUriPre, List<String> imgs,int id) {
        StringBuilder stringBuilder = new StringBuilder();
        final String l = System.getProperty("line.separator");
        String[] slice = html.split(l);
        List<String> fileName = new ArrayList<>();
        int length = slice.length;
        int size = imgs.size();
        int j = 0;
        for (int i = 0; i < length; i++) {
            if (j == size) {
                if (i != 0) {
                    stringBuilder.append(l);
                }
                stringBuilder.append(slice[i]);
                continue;
            }
            if (i != 0) {
                stringBuilder.append(l);
            }
            String target = imgs.get(j);
            if (slice[i].contains(target)) {
                String f =  id + target.substring(41);
                fileName.add(newsImagePath + f);
                String line = slice[i].replace(target, imageUriPre + f);
                j++;
                stringBuilder.append(line);
            } else {
                stringBuilder.append(slice[i]);
            }
        }
        downLoad(imgs,fileName);
        return stringBuilder.toString();
    }


    public String getBody(NewsDetail story,boolean isNight) {
        if (story == null) return "";
        return loadDataWithCSS(story.getBody(), story.getCss().get(0),isNight);
    }


    /**
     * 生成 html
     * @param loadData body数据
     * @param cssPath  CSS 数据
     * @param isNight  是否是夜间模式
     * @return html 内容
     */
    private String loadDataWithCSS(String loadData, String cssPath,boolean isNight) {
        String header = "<html><head><meta name=\"viewport\" content=\"width=device-width, " +
                "initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0\">" +
                "</meta><meta http-equiv=\"Content-Type\" content=\"text/html;" +
                " charset=utf-8\"></meta><link href=\"%s\"  rel=\"stylesheet\"/></head>";
        String body = loadData.replace("<div class=\"headline\">", "").replace("<div class=\"img-place-holder\">", "");
        String footer = "</body></html>";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(header, cssPath));
        sb.append(isNight ? "<body class=\"night\">" : "<body class=\"redback\">");
        sb.append(body);
        sb.append(footer);
        return sb.toString();
    }


    public void downLoad(List<String> imgs, List<String> fileNames) {
        DownLoad.getDownload().download(imgs, fileNames);
    }

    public void downLoad(String src, String fileName) {
        DownLoad.getDownload().downLoad(src,fileName);
    }

    public void SaveStory(String html, String htmlPath , String newsImagePath, String imageUriPre, List<String> images,int id) {
        String temp ;
        if (NetUtil.isNetworkConnected()) {
            temp = ReplaceImage(html, newsImagePath,imageUriPre,images,id);
        } else {
            temp = html;
        }
        FilerHelper.writeByPath(htmlPath, temp);
    }

    public void saveHead(NewsDetail mNewsDetail, String headImagePath) {
        FileManager.saveImage(mNewsDetail.getImage(), headImagePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Uri>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtil.toastShort(context.getString(R.string.save_error));
                    }

                    @Override
                    public void onNext(Uri uri) {

                    }
                });
    }

    /**
     * 分享
     */
    public void share(News mNews) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share));
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_from) + mNews.getTitle() + "，http://daily.zhihu.com/story/" + mNews.getId());
        context.startActivity(Intent.createChooser(intent, mNews.getTitle()));
    }



}
