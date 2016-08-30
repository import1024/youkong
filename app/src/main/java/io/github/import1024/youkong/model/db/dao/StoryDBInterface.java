package io.github.import1024.youkong.model.db.dao;

import java.util.List;

import io.github.import1024.youkong.model.bean.Story;
import rx.Observable;

/**
 * Created by redback on 8/24/16.
 */
public interface StoryDBInterface {
    public Observable<Boolean> deleteStory(int id);
    public Observable<Boolean> insertStory(Story story);
    public Observable<Boolean> deleteAll();
    public Observable<List<Story>> readAll();
    public Observable<Boolean> query(int id);
    public boolean deleteStory2(int id);
}
