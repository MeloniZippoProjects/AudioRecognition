package org.melonizippo.audiorecognition.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.melonizippo.audiorecognition.recognition.Fingerprint;

public class FingerprintsDatabaseAdapter
{
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private FingerprintsDatabaseHelper mDbHelper;

    public FingerprintsDatabaseAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new FingerprintsDatabaseHelper(mContext);
    }

    public FingerprintsDatabaseAdapter createDatabase() throws SQLException
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

    public FingerprintsDatabaseAdapter open() throws SQLException
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

    public void close()
    {
        mDbHelper.close();
    }

    private static final String TABLE_FINGERPRINTS = "fingerprints";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FINGERPRINT = "fingerprint";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_YEAR = "year";

    public Map<Integer, Fingerprint> getFingerprints()
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

    public AudioMetadata getMetadata(int id)
    {
        try
        {
            String sql = String.format("SELECT %s FROM %s WHERE %s",
                    COLUMN_TITLE + ", " + COLUMN_AUTHOR + ", " + COLUMN_YEAR,
                    TABLE_FINGERPRINTS,
                    COLUMN_ID + " = " + id);

            Cursor cursor = mDb.rawQuery(sql, null);
            if(cursor.moveToFirst())
            {
                AudioMetadata metadata = new AudioMetadata();
                metadata.Title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                metadata.Author = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR));
                metadata.Year = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR));

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
}