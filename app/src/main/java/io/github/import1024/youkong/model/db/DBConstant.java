package io.github.import1024.youkong.model.db;

/**
 * Created by import1024 on 16/3/14.
 */
public class DBConstant {
    public static final String DB_NAME = "read.db";
    public static final String DB_NAME_SAVE = "save.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_READ = "read";
    public static final String COLUMN_ID = "newid";

    public static final String CREATE_TABLE_READ = "create table " + TABLE_READ + "(" + COLUMN_ID + " text)";

    public static final String TABLE_SAVE = "stories";
    public static final String STORY_ID = "story_id";
    public static final String STORY_TITLE = "story_title";
    public static final String STORY_IMAGE_SOURCE = "story_img_source";
    public static final String STORY_IMAGE = "story_img";
    public static final String STORY_CONTENT = "story_content";
    public static final String CREATE_TABLE_SAVE = "create table " +
            TABLE_SAVE + " (" +
            "id integer primary key AUTOINCREMENT, "+
            STORY_ID + " integer not null unique, " +
            STORY_TITLE + " text not null," +
            STORY_IMAGE + " text not null ," +
            STORY_IMAGE_SOURCE + " text not null," +
            STORY_CONTENT + "  text not null )";
}
