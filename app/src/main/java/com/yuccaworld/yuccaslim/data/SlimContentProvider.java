package com.yuccaworld.yuccaslim.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Yung on 10/3/2017.
 */

public class SlimContentProvider extends ContentProvider {
    private SlimDBHelper mSlimDBHelper;

    public static final int ACTIVITY = 100;
    public static final int ACTIVITY_WITH_ID = 101;
    public static final int USER = 200;
    public static final int USER_WITH_ID = 201;
    public static final int FOOD = 300;
    public static final int FOOD_WITH_ID = 301;
    private static UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher((UriMatcher.NO_MATCH));
        uriMatcher.addURI(SlimContract.AUTHORITY, SlimContract.PATH_ACTIVITY, ACTIVITY);
        uriMatcher.addURI(SlimContract.AUTHORITY, SlimContract.PATH_ACTIVITY + "/#", ACTIVITY_WITH_ID);
        uriMatcher.addURI(SlimContract.AUTHORITY, SlimContract.PATH_USER, USER);
        uriMatcher.addURI(SlimContract.AUTHORITY, SlimContract.PATH_USER + "/#", USER_WITH_ID);
        uriMatcher.addURI(SlimContract.AUTHORITY, SlimContract.PATH_FOOD, FOOD);
        uriMatcher.addURI(SlimContract.AUTHORITY, SlimContract.PATH_FOOD + "/#", FOOD_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mSlimDBHelper = new SlimDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mSlimDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case ACTIVITY:
                retCursor = db.query(SlimContract.SlimDB.VIEW_TODAY_ACTIVITY,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ACTIVITY_WITH_ID:
                // URI: content://<authority>/activity/#
                // get(1) means the "/# "part
                String id = uri.getPathSegments().get(1);

                //Selection is the _ID column = ? and the selection args = the row id from the URI
                String selectionString = "_id=?";
                String[] SelectionArgsArray = new String[]{id};
                retCursor = db.query(SlimContract.SlimDB.VIEW_TODAY_ACTIVITY,
                        projection,
                        selectionString,
                        SelectionArgsArray,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FOOD:
                retCursor = db.query(SlimContract.SlimDB.TABLE_FOOD,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unkonwn uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase database = mSlimDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        long id;
        switch (match) {
            case ACTIVITY:
                id = database.insertOrThrow(SlimContract.SlimDB.TABLE_ACTIVITY, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(SlimContract.SlimDB.CONTENT_ACTIVITY_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
                break;
            case USER:
                id = database.insert(SlimContract.SlimDB.TABLE_USER, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(SlimContract.SlimDB.CONTENT_USER_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mSlimDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int activityDeleted=0;

        switch (match) {
            case ACTIVITY_WITH_ID:
                String id = uri.getPathSegments().get(1);
                activityDeleted = db.delete(SlimContract.SlimDB.TABLE_ACTIVITY, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri" + uri);
        }
        if (activityDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return activityDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mSlimDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int activityUpdated=0;
        switch (match) {
            case ACTIVITY_WITH_ID:
                String id = uri.getPathSegments().get(1);
                activityUpdated = db.update(SlimContract.SlimDB.TABLE_ACTIVITY, contentValues, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri" + uri);
        }
        if (activityUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return activityUpdated;
    }
}
