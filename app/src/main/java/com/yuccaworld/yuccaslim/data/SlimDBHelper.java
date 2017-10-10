package com.yuccaworld.yuccaslim.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yuccaworld.yuccaslim.data.SlimContract.SlimDB;

/**
 * Created by Yung on 8/2/2017.
 */

public class SlimDBHelper extends SQLiteOpenHelper {

    // COMPLETED (2) Create a static final String called DATABASE_NAME and set it to "slimDB.db"
    // The database name
    private static final String DATABASE_NAME = "slimDB.db";

    // COMPLETED (3) Create a static final int called DATABASE_VERSION and set it to 1
    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 3;

    public SlimDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // COMPLETED (6) Inside, create an String query called SQL_CREATE_SLIMDB_TABLE that will create the table
        // Create User table
        final String SQL_CREATE_SLIMDB_TABLE_USER = "CREATE TABLE " + SlimDB.TABLE_USER + " (" +
                SlimDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SlimDB.COLUMN_USER_ID + " GUID NOT NULL, " +
                SlimDB.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                SlimDB.COLUMN_FIRST_NAME + " TEXT, " +
                SlimDB.COLUMN_LAST_NAME + " TEXT, " +
                SlimDB.COLUMN_EMAIL + " TEXT, " +
                SlimDB.COLUMN_WEIGHT + " REAL NOT NULL, " +
                SlimDB.COLUMN_AGE + " INTEGER NOT NULL, " +
                SlimDB.COLUMN_GENDER + " TEXT NOT NULL, " +
                SlimDB.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        // Create Activity table
        final String SQL_CREATE_SLIMDB_TABLE_ACTIVITY = "CREATE TABLE " + SlimDB.TABLE_ACTIVITY + " (" +
                SlimDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SlimDB.COLUMN_ACTIVITY_ID + " GUID NOT NULL, " +
                SlimDB.COLUMN_USER_ID + " GUID, " +
                SlimDB.COLUMN_ATIVITY_TYPE_ID + " INTEGER NOT NULL, " +
                SlimDB.COLUMN_ACTIVITY_TIME + " TIMESTAMP, " +
                SlimDB.COLUMN_VALUE_DECIMAL + " REAL, " +
                SlimDB.COLUMN_VALUE_INT + " INTEGER, " +
                SlimDB.COLUMN_VALUE_TEXT + " TEXT, " +
                SlimDB.COLUMN_ITEM_ID + " GUID, " +
                SlimDB.COLUMN_HINT_ID + " TEXT NOT NULL, " +
                SlimDB.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        // Create Today Activity View
        /*
        CREATE VIEW vTodayActivity AS
        SELECT
                   A.activity_id
                   ,A.user_id
                   ,A.activity_time
                   ,A.activity_type_id
                   ,A.value_decimal
                   ,A.value_int
                   ,A.value_text
                   ,A.item_id
                   ,A.hint_id
                   ,AT.activity_type_desc
                   ,AT.icon_image_path
        FROM ACTIVITY A INNER JOIN ActivityType AT ON A.activity_type_id = AT.activity_type_id
         */
        final String SQL_CREATE_SLIMDB_VIEW_TODAY_ACTIVITY = "CREATE VIEW " + SlimDB.VIEW_TODAY_ACTIVITY + " AS SELECT" +
                " A." + SlimDB._ID + " as " + SlimDB._ID +
                ", A." + SlimDB.COLUMN_ACTIVITY_ID + " as " + SlimDB.COLUMN_ACTIVITY_ID +
                ", A." + SlimDB.COLUMN_USER_ID + " as " + SlimDB.COLUMN_USER_ID +
                ", A." + SlimDB.COLUMN_ATIVITY_TYPE_ID + " as " + SlimDB.COLUMN_ATIVITY_TYPE_ID +
                ", A." + SlimDB.COLUMN_ACTIVITY_TIME + " as " + SlimDB.COLUMN_ACTIVITY_TIME +
                ", A." + SlimDB.COLUMN_VALUE_DECIMAL + " as " + SlimDB.COLUMN_VALUE_DECIMAL +
                ", A." + SlimDB.COLUMN_VALUE_INT + " as " + SlimDB.COLUMN_VALUE_INT +
                ", A." + SlimDB.COLUMN_VALUE_TEXT + " as " + SlimDB.COLUMN_VALUE_TEXT +
                ", A." + SlimDB.COLUMN_ITEM_ID + " as " + SlimDB.COLUMN_ITEM_ID +
                ", A." + SlimDB.COLUMN_HINT_ID + " as " + SlimDB.COLUMN_HINT_ID +
                ", AT." + SlimDB.COLUMN_ATIVITY_TYPE_DESC + " as " + SlimDB.COLUMN_ATIVITY_TYPE_DESC +
                ", AT." + SlimDB.COLUMN_ICON_IMAGE_PATH + " as " + SlimDB.COLUMN_ICON_IMAGE_PATH +
                " FROM " + SlimDB.TABLE_ACTIVITY + " A " +
                " INNER JOIN " + SlimDB.TABLE_ACTIVITY_TYPE + " AT ON A." + SlimDB.COLUMN_ATIVITY_TYPE_ID + " = AT." + SlimDB.COLUMN_ATIVITY_TYPE_ID +
                ";";
        Log.v("SQL View: ", SQL_CREATE_SLIMDB_VIEW_TODAY_ACTIVITY);

        // Create User table
        final String SQL_CREATE_SLIMDB_TABLE_ACTIVITY_TYPE = "CREATE TABLE " + SlimDB.TABLE_ACTIVITY_TYPE + " (" +
                SlimDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SlimDB.COLUMN_ATIVITY_TYPE_ID + " INTEGER NOT NULL, " +
                SlimDB.COLUMN_ATIVITY_TYPE_DESC + " TEXT, " +
                SlimDB.COLUMN_ICON_IMAGE_PATH + " TEXT, " +
                SlimDB.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        // COMPLETED (7) Execute the query by calling execSQL on sqLiteDatabase and pass the string query SQL_CREATE_SLIMDB_TABLE
        try {
            db.execSQL(SQL_CREATE_SLIMDB_TABLE_USER);
            db.execSQL(SQL_CREATE_SLIMDB_TABLE_ACTIVITY);
            db.execSQL(SQL_CREATE_SLIMDB_TABLE_ACTIVITY_TYPE);
            db.execSQL(SQL_CREATE_SLIMDB_VIEW_TODAY_ACTIVITY);
        } catch (SQLException e) {
            Log.e("SQL error", e.toString());
        }

        // Init the DB with meta data
        // Init Table ActivityType;
        ContentValues values = new ContentValues();
        values.put(SlimDB.COLUMN_ATIVITY_TYPE_ID, 1);
        values.put(SlimDB.COLUMN_ATIVITY_TYPE_DESC, "Weight");
        values.put(SlimDB.COLUMN_ICON_IMAGE_PATH, "@drawable/ic_weight_24px");

        try {
            long newRowId = db.insertOrThrow(SlimDB.TABLE_ACTIVITY_TYPE, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SlimDB.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + SlimDB.TABLE_ACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + SlimDB.TABLE_ACTIVITY_TYPE);
        db.execSQL("DROP VIEW IF EXISTS " + SlimDB.VIEW_TODAY_ACTIVITY);
        onCreate(db);
    }
}
