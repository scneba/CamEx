package com.thestk.camex.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thestk.camex.database.DataContract.DataEntry;

/**
 * Created by Neba on 10-Oct-17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 1;
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +

                /*
                 * WeatherEntry did not explicitly declare a column called "_ID". However,
                 * WeatherEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        DataEntry.COLUMN_DATA_ID + " INTEGER NOT NULL, " +

                        DataEntry.COLUMN_TYPE + " TEXT NOT NULL," +

                        DataEntry.COLUMN_TITLE + " TEXT NOT NULL, " +

                        DataEntry.COLUMN_ARTIST_ACTORS + " TEXT NOT NULL, " +

                        DataEntry.COLUMN_MUSIC_GENRE + " TEXT NOT NULL, " +

                        DataEntry.COLUMN_YOUTUBE_ID + " TEXT NOT NULL, " +
                        DataEntry.COLUMN_VIEWS + " TEXT NOT NULL, " +

                        DataEntry.COLUMN_LIKES + " TEXT NOT NULL, " +
                        DataEntry.COLUMN_IMAGE + " TEXT NOT NULL, " +
                        DataEntry.COLUMN_ASPECT_RATIO + " REAL NOT NULL, " +

                        /**
                         * make data ID unique and replace on conflict*/
                        " UNIQUE (" + DataEntry.COLUMN_DATA_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop and recreate table for now
        db.execSQL("DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME);
        onCreate(db);
    }
}
