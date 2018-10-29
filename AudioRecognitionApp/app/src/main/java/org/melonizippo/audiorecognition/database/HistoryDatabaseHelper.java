package org.melonizippo.audiorecognition.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDatabaseHelper extends SQLiteOpenHelper
{
    public static final String DB_NAME = "history.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "history";
    public static final String ID_NAME = "id";
    public static final String TIMESTAMP_NAME = "timestamp";
    public static final String SONG_ID_NAME = "song_id";


    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            ID_NAME + "INTEGER PRIMARY KEY," +
            TIMESTAMP_NAME + " TEXT," +
            SONG_ID_NAME + " INTEGER)";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public HistoryDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
