package com.yuccaworld.yuccaslim.utilities;

import com.yuccaworld.yuccaslim.data.ActivityInfo;

import java.sql.Timestamp;

/**
 * Created by Yung on 9/3/2017.
 */

public class FakeDataUtils {
    public static ActivityInfo genFakeActivityInfo() {
        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.activityDescription = "Dummy Activity";
        activityInfo.hint = "Dummy Hint";
        long now = System.currentTimeMillis();
        activityInfo.activityTime = new Timestamp(now);
        return activityInfo;
    }

}
