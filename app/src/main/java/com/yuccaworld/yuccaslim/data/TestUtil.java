package com.yuccaworld.yuccaslim.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yung on 8/3/2017.
 */

public class TestUtil {
    public static void insertFakeData(SQLiteDatabase db){
        if (db ==null){
            return;
        }
        //create a list of fake users
        List<ContentValues> list = new ArrayList<ContentValues>();
        ContentValues cv = new ContentValues();
        cv.put(SlimContract.SlimDB.COLUMN_USER_ID, "");
        cv.put(SlimContract.SlimDB.COLUMN_AGE, 18);
        cv.put(SlimContract.SlimDB.COLUMN_FIRST_NAME, "New");
        cv.put(SlimContract.SlimDB.COLUMN_LAST_NAME, "Friend");
        cv.put(SlimContract.SlimDB.COLUMN_GENDER, "M");
        cv.put(SlimContract.SlimDB.COLUMN_WEIGHT, 0.11);
        //cv.put(SlimContract.SlimDB.COLUMN_TIMESTAMP, );
        list.add(cv);
        cv = new ContentValues();
        cv.put(SlimContract.SlimDB.COLUMN_USER_ID, "");
        cv.put(SlimContract.SlimDB.COLUMN_AGE, 18);
        cv.put(SlimContract.SlimDB.COLUMN_FIRST_NAME, "Old");
        cv.put(SlimContract.SlimDB.COLUMN_LAST_NAME, "Pal");
        cv.put(SlimContract.SlimDB.COLUMN_GENDER, "F");
        cv.put(SlimContract.SlimDB.COLUMN_WEIGHT, 1.11);
        //cv.put(SlimContract.SlimDB.COLUMN_TIMESTAMP, );
        list.add(cv);
        //test

        //insert all user in one transaction
        try{
            db.beginTransaction();
            int delete = db.delete(SlimContract.SlimDB.TABLE_NAME,null,null);
            for (ContentValues c: list){
                db.insert(SlimContract.SlimDB.TABLE_NAME,null,c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e){

        }
        finally {
            db.endTransaction();
        }

    }
}
