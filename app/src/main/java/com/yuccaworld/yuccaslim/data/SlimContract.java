package com.yuccaworld.yuccaslim.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Yung on 8/2/2017.
 */

public class SlimContract {
    public static final String AUTHORITY = "com.yuccaworld.yuccaslim";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_ACTIVITY = "Activity";
    public static final String PATH_USER = "User";
    // Create an inner  class that implements the BaseColumns interface
    public static final class SlimDB implements BaseColumns {
        public static final Uri CONTENT_USER_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();
        public static final Uri CONTENT_ACTIVITY_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIVITY).build();
        public static final String TABLE_USER = "User";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static final String TABLE_ACTIVITY = "Activity";
        public static final String COLUMN_ACTIVITY_ID = "activity_id";
        public static final String COLUMN_ATIVITY_TYPE_ID = "activity_type_id";
        public static final String COLUMN_ACTIVITY_TIME = "activity_time";
        public static final String COLUMN_HINT_ID = "hint_id";

    }
}
