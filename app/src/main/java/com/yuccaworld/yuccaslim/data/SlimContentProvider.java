package com.yuccaworld.yuccaslim.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
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
    private static UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher((UriMatcher.NO_MATCH));
        uriMatcher.addURI(SlimContract.AUTHORITY, SlimContract.PATH_ACTIVITY, ACTIVITY);
        uriMatcher.addURI(SlimContract.AUTHORITY, SlimContract.PATH_ACTIVITY + "/#", ACTIVITY_WITH_ID);
        uriMatcher.addURI(SlimContract.AUTHORITY, SlimContract.PATH_USER, USER);
        uriMatcher.addURI(SlimContract.AUTHORITY, SlimContract.PATH_USER + "/#", USER_WITH_ID);
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
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
