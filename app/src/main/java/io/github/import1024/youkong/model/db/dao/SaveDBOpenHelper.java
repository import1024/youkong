package io.github.import1024.youkong.model.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.github.import1024.youkong.model.db.DBConstant;

/**
 * Created by redback on 8/24/16.
 */
public class SaveDBOpenHelper extends SQLiteOpenHelper {
    private static SaveDBOpenHelper instance;

    private SaveDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstant.CREATE_TABLE_SAVE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static SaveDBOpenHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SaveDBOpenHelper.class) {
                if (instance == null) {
                    instance = new SaveDBOpenHelper(context.getApplicationContext(), DBConstant.DB_NAME_SAVE, null, DBConstant.DB_VERSION);
                }
            }
        }
        return instance;
    }
}
