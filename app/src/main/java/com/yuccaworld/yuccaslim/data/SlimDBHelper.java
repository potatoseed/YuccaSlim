package com.yuccaworld.yuccaslim.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yuccaworld.yuccaslim.data.SlimContract.*;

/**
 * Created by Yung on 8/2/2017.
 */

public class SlimDBHelper extends SQLiteOpenHelper {

    // COMPLETED (2) Create a static final String called DATABASE_NAME and set it to "slimDB.db"
    // The database name
    private static final String DATABASE_NAME = "slimDB.db";

    // COMPLETED (3) Create a static final int called DATABASE_VERSION and set it to 1
    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    public SlimDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // COMPLETED (6) Inside, create an String query called SQL_CREATE_SLIMDB_TABLE that will create the table
        // Create a table to hold waitlist data
        final String SQL_CREATE_SLIMDB_TABLE = "CREATE TABLE " + SlimDB.TABLE_NAME + " (" +
                SlimDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SlimDB.COLUMN_USER_ID + " TEXT NOT NULL, " +
                SlimDB.COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                SlimDB.COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                SlimDB.COLUMN_WEIGHT + " REAL NOT NULL, " +
                SlimDB.COLUMN_AGE + " INTEGER NOT NULL, " +
                SlimDB.COLUMN_GENDER + " TEXT NOT NULL, " +
                SlimDB.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        // COMPLETED (7) Execute the query by calling execSQL on sqLiteDatabase and pass the string query SQL_CREATE_SLIMDB_TABLE
        db.execSQL(SQL_CREATE_SLIMDB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}