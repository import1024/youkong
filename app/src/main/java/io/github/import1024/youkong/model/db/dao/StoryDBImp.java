package io.github.import1024.youkong.model.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.import1024.youkong.model.bean.Story;
import io.github.import1024.youkong.model.db.DBConstant;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by redback on 8/24/16.
 */
public class StoryDBImp implements StoryDBInterface {

    private SaveDBOpenHelper helper;

    public StoryDBImp(Context context) {
        this.helper = SaveDBOpenHelper.getInstance(context);
    }

    @Override
    public Observable<Boolean> deleteStory(int id) {
        return Observable.fromCallable(() -> delete(id)).subscribeOn(Schedulers.io());
    }

    private boolean delete(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String selection = DBConstant.STORY_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        long insertNum = db.delete(DBConstant.TABLE_SAVE, selection, selectionArgs);
        if (insertNum > 0){
            return true;
        }
        return false;
    }

    @Override
    public Observable<Boolean> insertStory(Story story) {
        return Observable.fromCallable(()->insert(story)).observeOn(Schedulers.io());
    }

    private boolean insert(Story story) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBConstant.STORY_ID, story.id);
        values.put(DBConstant.STORY_TITLE, story.title);
        values.put(DBConstant.STORY_IMAGE, story.image);
        values.put(DBConstant.STORY_IMAGE_SOURCE,story.imageSource);
        values.put(DBConstant.STORY_CONTENT, story.content);
        long newRowId = db.insert(DBConstant.TABLE_SAVE, null, values);
        if (newRowId > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Observable<Boolean> deleteAll() {
        return Observable.fromCallable(() -> delete()).subscribeOn(Schedulers.io());
    }

    private boolean delete() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "delete from " + DBConstant.TABLE_SAVE;
        Log.d("keep","delete : "+sql);
        db.execSQL(sql);
        return true;
    }



    @Override
    public Observable<List<Story>> readAll() {
        return Observable.fromCallable(() -> read()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> query(int id) {
        return Observable.fromCallable(() -> Qeury(id)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public boolean deleteStory2(int id) {
        return delete(id);
    }

    private boolean Qeury(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select " + DBConstant.STORY_ID + " from " + DBConstant.TABLE_SAVE + " where " + DBConstant.STORY_ID + " = " + String.valueOf(id);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d("test"," Qeurt--------------is True");
            cursor.close();
            return true;
        } else {
            Log.d("test"," Qeury--------------is False");
            cursor.close();
            return false;
        }
    }


    private List<Story> read() {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select * from " + DBConstant.TABLE_SAVE;
        Cursor cursor = null;
        List<Story> stories = null;
        db.beginTransaction();
        cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            stories = new ArrayList<>();
            Story story = null;
            do {
                story = new Story();
                story.id = cursor.getInt(cursor.getColumnIndex(DBConstant.STORY_ID));
                story.title = cursor.getString(cursor.getColumnIndex(DBConstant.STORY_TITLE));
                story.image = cursor.getString(cursor.getColumnIndex(DBConstant.STORY_IMAGE));
                story.imageSource = cursor.getString(cursor.getColumnIndex(DBConstant.STORY_IMAGE_SOURCE));
                story.content = cursor.getString(cursor.getColumnIndex(DBConstant.STORY_CONTENT));
                stories.add(story);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        db.endTransaction();
        return stories;
    }
}
