package org.melonizippo.audiorecognition.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HistoryDatabaseAdapter
{
    private HistoryDatabaseAdapter()
    {
    }

    public static List<HistoryEntry> getHistory(Context context)
    {
        HistoryDatabaseHelper dbHelper = new HistoryDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query =
                "SELECT " + HistoryDatabaseHelper.TIMESTAMP_NAME + ", " + HistoryDatabaseHelper.SONG_ID_NAME +
                        " FROM " + HistoryDatabaseHelper.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        List<HistoryEntry> history = new LinkedList<>();
        int tsIndex = cursor.getColumnIndex(HistoryDatabaseHelper.TIMESTAMP_NAME);
        int songIdIndex = cursor.getColumnIndex(HistoryDatabaseHelper.SONG_ID_NAME);
        while (cursor.moveToNext())
        {
            HistoryEntry entry = new HistoryEntry();
            entry.timestamp = LocalDate.parse(cursor.getString(tsIndex));
            entry.songId = cursor.getInt(songIdIndex);
            history.add(entry);
        }
        cursor.close();
        dbHelper.close();

        Collections.reverse(history);

        return history;
    }

    public static void addEntry(Context context, HistoryEntry entry)
    {
        HistoryDatabaseHelper dbHelper = new HistoryDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HistoryDatabaseHelper.TIMESTAMP_NAME, entry.timestamp.toString());
        values.put(HistoryDatabaseHelper.SONG_ID_NAME, entry.songId);

        db.insert(HistoryDatabaseHelper.TABLE_NAME, null, values);
        dbHelper.close();
    }

    public static void clearDatabase(Context context)
    {
        HistoryDatabaseHelper dbHelper = new HistoryDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(HistoryDatabaseHelper.SQL_DELETE_TABLE);
        dbHelper.close();
    }
}
