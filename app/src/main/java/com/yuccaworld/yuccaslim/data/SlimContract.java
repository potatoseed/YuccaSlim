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
    public static final String PATH_FOOD = "Food";
    // Create an inner  class that implements the BaseColumns interface
    public static final class SlimDB implements BaseColumns {
        public static final Uri CONTENT_USER_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();
        public static final Uri CONTENT_ACTIVITY_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIVITY).build();
        public static final Uri CONTENT_FOOD_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOOD).build();

        // Table User
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

        // Table Activity
        public static final String TABLE_ACTIVITY = "Activity";
        public static final String COLUMN_ACTIVITY_ID = "activity_id";
        public static final String COLUMN_ATIVITY_TYPE_ID = "activity_type_id";
        public static final String COLUMN_ACTIVITY_TIME = "activity_time";
        public static final String COLUMN_VALUE_DECIMAL = "value_decimal";
        public static final String COLUMN_VALUE_INT = "value_int";
        public static final String COLUMN_VALUE_TEXT = "value_text";
        public static final String COLUMN_ITEM_ID = "item_id";
        public static final String COLUMN_HINT_ID = "hint_id";

        // Table Activity_Type
        public static final String TABLE_ACTIVITY_TYPE = "ActivityType";
        public static final String COLUMN_ATIVITY_TYPE_DESC = "activity_type_desc";
        public static final String COLUMN_ICON_IMAGE_PATH = "icon_image_path";

        // Table Food
        public static final String TABLE_FOOD = "Food";
        public static final String COLUMN_FOOD_ID = "food_id";
        public static final String COLUMN_FOOD_NAME = "food_name";

        // View TodayActivity
        public static final String VIEW_TODAY_ACTIVITY = "vTodayActivity";

        /**
         * Builds a URI that adds the activity id to the end of the today activity URI path.
         * This is used to query details about a single activity entry . This is what we
         * use for the detail view query.
         *
         * @param rowID Row id in the cursor _id
         * @return Uri to query details about a single weather entry
         */
        public static Uri buildWeightAdd(int rowId){
            return CONTENT_ACTIVITY_URI.buildUpon().appendPath(Integer.toString(rowId)).build();
        }

        public static Uri buildWeightEdit(int rowID){
            return CONTENT_ACTIVITY_URI.buildUpon().appendPath(Integer.toString(rowID)).build();
        }
    }
}
