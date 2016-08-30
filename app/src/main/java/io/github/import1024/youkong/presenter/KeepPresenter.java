package io.github.import1024.youkong.presenter;

import android.content.Context;

import java.util.List;

import io.github.import1024.youkong.model.bean.Story;
import io.github.import1024.youkong.model.db.dao.StoryDBImp;
import io.github.import1024.youkong.model.db.dao.StoryDBInterface;
import rx.Observable;

/**
 * Created by redback on 8/25/16.
 */
public class KeepPresenter {
    Context context;
    StoryDBInterface storyDBInterface;

    public KeepPresenter(Context context) {
        this.context = context;
        storyDBInterface = new StoryDBImp(context);
    }

    public Observable<List<Story>> getAllStories() {
        return storyDBInterface.readAll();
    }

    public Observable<Boolean> deleteAll() {
        return storyDBInterface.deleteAll();
    }

    public boolean deleteStory2(int id) {
        return storyDBInterface.deleteStory2(id);
    }

    public Observable<Boolean> deleteStory(int id) {
        return storyDBInterface.deleteStory(id);
    }
}
