package com.eklee.timestamptable.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Judy on 2018-01-18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static DatabaseHelper sInstance;

    private  static String DB_NAME = "timestamptable.db";
    private  static int DB_VERSION = 2;
    private static SQLiteDatabase mDb;
    private Context mContext;
    public static DatabaseDAO mDao;


    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            Log.d(TAG, "getInstance: creating instance");
            sInstance = new DatabaseHelper(context.getApplicationContext());
            mDb = sInstance.getWritableDatabase();
            mDao = new DatabaseDAO(mDb);
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: creating db table");
        db.beginTransaction();
        try {
            db.execSQL(IDatabaseSchema.SETTINGS_TABLE_CREATE);
            db.execSQL(IDatabaseSchema.FAVORITE_TABLE_CREATE);
            db.setTransactionSuccessful();
        } catch (SQLException e){
            Log.e(TAG, "onCreate: SQLException" + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: called. oldVersion: " + oldVersion + " newVersion: " + newVersion);
        // 버전 1일 때만 favorite 칼럼 추가, favorite 테이블 추가
        switch (oldVersion) {
            case 1 :
                try {
                    db.beginTransaction();
                    db.execSQL("ALTER TABLE " + IDatabaseSchema.SETTINGS_TABLE + " ADD COLUMN " + IDatabaseSchema.SAVE_USING_FAVORITE + " TEXT DEFAULT ''");
                    db.execSQL(IDatabaseSchema.FAVORITE_TABLE_CREATE);
                    db.setTransactionSuccessful();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "onUpgrade: " + e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
        }


//            Log.w(TAG, "Upgrading database from version "
//                    + oldVersion + " to "
//                    + newVersion + " which destroys all old data");
//
//            db.execSQL("DROP TABLE IF EXISTS " + IDatabaseSchema.SETTINGS_TABLE_CREATE);
//
//            onCreate(db);

    }

    @Override
    public synchronized void close() {
        if (sInstance != null)
            mDb.close();
    }


}
