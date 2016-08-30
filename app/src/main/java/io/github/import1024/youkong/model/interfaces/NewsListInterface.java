package io.github.import1024.youkong.model.interfaces;

import io.github.import1024.youkong.model.bean.NewsList;

/**
 * interface callback for NewsPresent
 * Created by redback on 8/29/16.
 */
public interface NewsListInterface {
    void refresh(boolean flag);

    void loadBeforeNewsError();

    void loadBeforeNewsSuccess(NewsList news);

    void loadLatestNewsError();

    void loadLatestNewsSuccess(NewsList news);
}
