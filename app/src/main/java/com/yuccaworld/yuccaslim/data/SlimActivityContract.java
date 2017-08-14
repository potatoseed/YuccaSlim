package com.yuccaworld.yuccaslim.data;

import android.provider.BaseColumns;

/**
 * Created by Yung on 9/3/2017.
 */

public class SlimActivityContract {
    public static final class SlimDB implements BaseColumns {
        public static final String TABLE_NAME = "Activity";
        public static final String COLUMN_ACTIVITY_ID = "activity_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_ATIVITY_TYPE_ID = "activity_type_id";
        public static final String COLUMN_ACTIVITY_TIME = "activity_time";
        public static final String COLUMN_HINT_ID = "hint_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
