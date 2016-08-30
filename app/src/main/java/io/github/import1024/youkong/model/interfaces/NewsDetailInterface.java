package io.github.import1024.youkong.model.interfaces;

import io.github.import1024.youkong.model.bean.NewsDetail;

/**
 * callback for NewsDetailPresenter
 * Created by redback on 8/29/16.
 */
public interface NewsDetailInterface {
    void loadDataSuccess(NewsDetail newsDetail);
    void loadDataError(Throwable throwable);
}
