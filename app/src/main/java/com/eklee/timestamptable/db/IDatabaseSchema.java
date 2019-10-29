package com.eklee.timestamptable.db;

/**
 * Created by Judy on 2018-04-08.
 */

public interface IDatabaseSchema {

    String SETTINGS_TABLE = "TB_SETTINGS";
    String FAVORITE_TABLE = "TB_FAVORITE";
    String ID = "_id";

    String SAVE_ORIGINAL_PHOTO = "SAVE_ORIGINAL_PHOTO";
    String SAVE_USING_FAVORITE = "SAVE_USING_FAVORITE";
    String FAVORITE_STYLE = "FAVORITE_STYLE";
    String FAVORITE_FONT = "FAVORITE_FONT";
    String FAVORITE_SIZE = "FAVORITE_SIZE";
    String FAVORITE_COLOR = "FAVORITE_COLOR";

    String SETTINGS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + SETTINGS_TABLE
            + " ("
            + ID
            + " INTEGER PRIMARY KEY, "
            + SAVE_ORIGINAL_PHOTO
            + " TEXT NOT NULL, "
            + SAVE_USING_FAVORITE
            + " TEXT NOT NULL "
            + ")";

    String FAVORITE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + FAVORITE_TABLE
            + " ("
            + ID
            + " INTEGER PRIMARY KEY, "
            + FAVORITE_STYLE
            + " TEXT, "
            + FAVORITE_FONT
            + " TEXT, "
            + FAVORITE_SIZE
            + " TEXT, "
            + FAVORITE_COLOR
            + " TEXT "
            + ")";

//    String[] HISTORY_COLUMNS = new String[] { ID, SAVE_ORIGINAL_PHOTO };
}
