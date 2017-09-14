package com.yuccaworld.yuccaslim.utilities;

import com.yuccaworld.yuccaslim.model.ActivityInfo;

import java.sql.Timestamp;

/**
 * Created by Yung on 9/3/2017.
 */

public class SlimUtils {
    public static ActivityInfo genFakeActivityInfo() {
        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.setActivityDescription("Dummy Activity");
        activityInfo.setHint("Dummy Hint");
        long now = System.currentTimeMillis();
        activityInfo.setActivityTime(new Timestamp(now));
        return activityInfo;
    }

    // Get activity type icon
    public static int getActivityTypeIcon() {
        return 0;
    }

}
