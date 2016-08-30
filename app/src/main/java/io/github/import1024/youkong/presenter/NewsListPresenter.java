package io.github.import1024.youkong.presenter;

import android.content.Context;

import java.util.List;

import io.github.import1024.youkong.model.bean.News;
import io.github.import1024.youkong.model.bean.NewsList;
import io.github.import1024.youkong.model.db.dao.NewDao;
import io.github.import1024.youkong.model.interfaces.NewsListInterface;
import io.github.import1024.youkong.common.network.manager.RetrofitManager;
import io.github.import1024.youkong.common.utils.NetUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * presenter for NewsListFragment
 * Created by redback on 8/29/16.
 */
public class NewsListPresenter {

    Context context;
    NewsListInterface newsListInterface;

    public NewsListPresenter(Context context, NewsListInterface newsListInterface) {
        this.context = context;
        this.newsListInterface = newsListInterface;
    }

    public void loadLatestNews() {
        RetrofitManager.builder().getLatestNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> newsListInterface.refresh(true))
                .map(newsList ->{
                    cacheAllDetail(newsList.getStories());
                    return changeReadState(newsList);
                })
                .subscribe(newsList -> {
                    newsListInterface.loadLatestNewsSuccess(newsList);
                }, throwable ->  {
                   newsListInterface.loadLatestNewsError();
                });

    }

    public void loadBeforeNews(String date) {
        RetrofitManager.builder().getBeforeNews(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(newsList -> {
                    cacheAllDetail(newsList.getStories());
                    return changeReadState(newsList);
                })
                .subscribe(newsList -> {
                    newsListInterface.loadBeforeNewsSuccess(newsList);
                }, throwable -> {
                    newsListInterface.loadBeforeNewsError();
                });
    }

    private void cacheAllDetail(List<News> newsList) {
        if (NetUtil.isWifiConnected()) {
            for (News news : newsList) {
                cacheNewsDetail(news.getId());
            }
        }
    }

    private void cacheNewsDetail(int newsId) {
        RetrofitManager.builder().getNewsDetail(newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(newsDetail -> {});
    }


    public NewsList changeReadState(NewsList newsList) {
        List<String> allReadId = new NewDao(context).getAllReadNew();
        for (News news : newsList.getStories()) {
            news.setDate(newsList.getDate());
            for (String readId : allReadId) {
                if (readId.equals(news.getId() + "")) {
                    news.setRead(true);
                }
            }
        }
        return newsList;
    }

}
