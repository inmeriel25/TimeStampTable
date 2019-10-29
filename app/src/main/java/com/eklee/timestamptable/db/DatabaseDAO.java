package com.eklee.timestamptable.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eklee.timestamptable.models.Settings;

import java.util.ArrayList;


/**
 * Created by Judy on 2018-01-19.
 */

public class DatabaseDAO extends DbContentProvider implements IDatabaseDAO, IDatabaseSchema{
    private static final String TAG = "DatabaseDAO";
    private Cursor cursor;
    private ContentValues initialValues;


    public DatabaseDAO(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public ArrayList<Settings> selectSettings() {
        Log.d(TAG, "selectSettings: called.");

        ArrayList<Settings> items = new ArrayList<>();
        cursor = mDb.query(SETTINGS_TABLE, null, null, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Settings settings = cursorToSettingsEntity(cursor);
                items.add(settings);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return items;
    }

    @Override
    public ArrayList<Settings> selectFavorite() {
        Log.d(TAG, "selectFavorite: called.");

        ArrayList<Settings> items = new ArrayList<>();
        cursor = mDb.query(FAVORITE_TABLE, null, null, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Settings settings = cursorToFavoriteEntity(cursor);
                items.add(settings);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return items;
    }

    private Settings cursorToSettingsEntity(Cursor cursor) {
        Log.d(TAG, "cursorToSettingsEntity: insert cursor data to model");

        Settings settings = new Settings();

        if (cursor != null) {
            if (cursor.getColumnIndex(ID) != -1) {
                settings.setId(cursor.getString(cursor.getColumnIndex(ID)));
            }
            if (cursor.getColumnIndex(SAVE_ORIGINAL_PHOTO) != -1) {
                settings.setSaveOriginal(cursor.getString(cursor.getColumnIndexOrThrow(SAVE_ORIGINAL_PHOTO)));
            }
            if (cursor.getColumnIndex(SAVE_USING_FAVORITE) != -1) {
                settings.setSaveFavorite(cursor.getString(cursor.getColumnIndexOrThrow(SAVE_USING_FAVORITE)));
            }
        }
        return settings;
    }

    private Settings cursorToFavoriteEntity(Cursor cursor) {
        Log.d(TAG, "cursorToSettingsEntity: insert cursor data to model");

        Settings settings = new Settings();

        if (cursor != null) {
            if (cursor.getColumnIndex(ID) != -1) {
                settings.setId(cursor.getString(cursor.getColumnIndex(ID)));
            }
            if (cursor.getColumnIndex(FAVORITE_STYLE) != -1) {
                settings.setStyle(cursor.getString(cursor.getColumnIndexOrThrow(FAVORITE_STYLE)));
            }
            if (cursor.getColumnIndex(FAVORITE_FONT) != -1) {
                settings.setFont(cursor.getString(cursor.getColumnIndexOrThrow(FAVORITE_FONT)));
            }
            if (cursor.getColumnIndex(FAVORITE_SIZE) != -1) {
                settings.setSize(cursor.getString(cursor.getColumnIndexOrThrow(FAVORITE_SIZE)));
            }
            if (cursor.getColumnIndex(FAVORITE_COLOR) != -1) {
                settings.setColor(cursor.getString(cursor.getColumnIndexOrThrow(FAVORITE_COLOR)));
            }
        }
        return settings;
    }

    public boolean addSettings(Settings settings) {
        Log.d(TAG, "addSettings: insert data to DB");
        // set values
        setSettingsContentValue(settings);
        try {
                super.delete(SETTINGS_TABLE, null, null);
                return super.insert(SETTINGS_TABLE, getContentValue()) > 0;
        } catch (SQLiteConstraintException ex){
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    private void setSettingsContentValue(Settings settings) {
        Log.d(TAG, "Settings setContentValue: insert data to ContentValues()");
        initialValues = new ContentValues();
        initialValues.put(SAVE_ORIGINAL_PHOTO, settings.getSaveOriginal());
        initialValues.put(SAVE_USING_FAVORITE, settings.getSaveFavorite());
    }


    public boolean addFavorite(Settings settings) {
        Log.d(TAG, "addFavorite: insert data to DB");
        // set values
        setFavoriteContentValue(settings);
        try {
            super.delete(FAVORITE_TABLE, null, null);
            return super.insert(FAVORITE_TABLE, getContentValue()) > 0;
        } catch (SQLiteConstraintException ex){
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    private void setFavoriteContentValue(Settings settings) {
        Log.d(TAG, "Favorite setContentValue: insert data to ContentValues()");
        initialValues = new ContentValues();
        initialValues.put(FAVORITE_STYLE, settings.getStyle());
        initialValues.put(FAVORITE_FONT, settings.getFont());
        initialValues.put(FAVORITE_SIZE, settings.getSize());
        initialValues.put(FAVORITE_COLOR, settings.getColor());
    }

    private ContentValues getContentValue() {
        return initialValues;
    }

}
