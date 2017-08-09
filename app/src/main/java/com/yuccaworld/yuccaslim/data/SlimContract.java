package com.yuccaworld.yuccaslim.data;

import android.provider.BaseColumns;

/**
 * Created by Yung on 8/2/2017.
 */

public class SlimContract {
    // TODO (1) Create an inner  class that implements the BaseColumns interface
    public static final class SlimDB implements BaseColumns {
        public static final String TABLE_NAME = "User";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "[last_name]";
        public static final String COLUMN_WEIGHT = "[weight]";
        public static final String COLUMN_AGE = "[age]";
        public static final String COLUMN_GENDER = "[gender]";
        public static final String COLUMN_TIMESTAMP = "[timestamp]";

    }
}
