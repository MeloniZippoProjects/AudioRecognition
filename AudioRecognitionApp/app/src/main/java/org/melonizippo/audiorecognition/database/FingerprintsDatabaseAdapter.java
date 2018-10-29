package org.melonizippo.audiorecognition.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;

import org.melonizippo.audiorecognition.recognition.Fingerprint;

public class FingerprintsDatabaseAdapter
{
    protected static final String TAG = "DataAdapter";

    private SQLiteDatabase mDb;
    private FingerprintsDatabaseHelper mDbHelper;

    public static Map<Integer, Fingerprint> getFingerprints(Context context)
    {
        FingerprintsDatabaseAdapter dbAdapter = new FingerprintsDatabaseAdapter(context);
        dbAdapter.createDatabase();
        dbAdapter.open();
        Map<Integer, Fingerprint> fingerprints = dbAdapter.getFingerprints();
        dbAdapter.close();

        return fingerprints;
    }

    public static SongMetadata getMetadata(int id, Context context)
    {
        FingerprintsDatabaseAdapter dbAdapter = new FingerprintsDatabaseAdapter(context);
        dbAdapter.createDatabase();
        dbAdapter.open();
        SongMetadata fingerprints = dbAdapter.getMetadata(id);
        dbAdapter.close();

        return fingerprints;
    }

    private FingerprintsDatabaseAdapter(Context context)
    {
        mDbHelper = new FingerprintsDatabaseHelper(context);
    }

    private FingerprintsDatabaseAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    private FingerprintsDatabaseAdapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    private void close()
    {
        mDbHelper.close();
    }

    private static final String TABLE_FINGERPRINTS = "song";

    private static final String COLUMN_ID = "song_id";
    private static final String COLUMN_FINGERPRINT = "fingerprint";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_ARTIST = "artist";
    private static final String COLUMN_ALBUM = "album";
    private static final String COLUMN_YEAR = "date";
    private static final String COLUMN_GENRE = "genre";
    private static final String COLUMN_COVER = "cover";

    private Map<Integer, Fingerprint> getFingerprints()
    {
        Cursor fingCursor = getFingerprintCursor();
        int idColumn = fingCursor.getColumnIndexOrThrow(COLUMN_ID);
        int fingerprintColumn = fingCursor.getColumnIndexOrThrow(COLUMN_FINGERPRINT);

        Map<Integer, Fingerprint> fingerprints = new HashMap<>();
        while(fingCursor.moveToNext())
        {
            int id = fingCursor.getInt(idColumn);
            byte[] fingerprintBytes = fingCursor.getBlob(fingerprintColumn);
            Fingerprint fingerprint = new Fingerprint(fingerprintBytes);

            fingerprints.put(id, fingerprint);
        }

        return fingerprints;
    }

    private Cursor getFingerprintCursor()
    {
        try
        {
            String sql = String.format("SELECT %s FROM %s",
                    COLUMN_ID + ", " + COLUMN_FINGERPRINT,
                    TABLE_FINGERPRINTS);

            return mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getFingerprintCursor >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    private SongMetadata getMetadata(int id)
    {
        try
        {
            String sql = String.format("SELECT %s FROM %s WHERE %s",
                    COLUMN_TITLE + ", " +
                    COLUMN_ARTIST + ", " +
                    COLUMN_ALBUM + ", " +
                    COLUMN_YEAR + ", " +
                    COLUMN_GENRE + ", " +
                    COLUMN_COVER,
                    TABLE_FINGERPRINTS,
                    COLUMN_ID + " = " + id);

            Cursor cursor = mDb.rawQuery(sql, null);
            if(cursor.moveToFirst())
            {
                SongMetadata metadata = new SongMetadata();
                metadata.title = getStringOrNull(cursor, COLUMN_TITLE);
                metadata.artist = getStringOrNull(cursor, COLUMN_ARTIST);
                metadata.album = getStringOrNull(cursor, COLUMN_ALBUM);
                metadata.year = getStringOrNull(cursor, COLUMN_YEAR);
                metadata.genre = getStringOrNull(cursor, COLUMN_GENRE);

                byte[] coverBytes = getBytesOrNull(cursor, COLUMN_COVER);
                if(coverBytes != null)
                    metadata.cover = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
                else
                    metadata.cover = null;

                return metadata;
            }
            else
                return null;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getMetadata >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    @Nullable
    private String getStringOrNull(Cursor cursor, String columnName)
    {
        try
        {
            int columnIndex = cursor.getColumnIndexOrThrow(columnName);
            if(cursor.isNull(columnIndex))
                return null;
            else
                return cursor.getString(columnIndex);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    @Nullable
    private byte[] getBytesOrNull(Cursor cursor, String columnName)
    {
        try
        {
            int columnIndex = cursor.getColumnIndexOrThrow(columnName);
            if(cursor.isNull(columnIndex))
                return null;
            else
                return cursor.getBlob(columnIndex);
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}