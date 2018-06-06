package com.yuccaworld.yuccaslim.model;

import java.sql.Timestamp;

/**
 * Created by Yung on 9/3/2017.
 */

public class ActivityInfo {
    private String activityType;
    private String activityDescription;
    private String hint;
    private Timestamp activityTime;

    public ActivityInfo() {

    }

    public ActivityInfo(String activityType, String activityDescription, String hint, Timestamp activityTime) {
        this.activityType = activityType;
        this.activityDescription = activityDescription;
        this.hint = hint;
        this.activityTime = activityTime;
    }

    public String getHint() {
        return hint;
    }
    public void setHint(String hint) {
        this.hint = hint;
    }
    public Timestamp getActivityTime() {
        return activityTime;
    }
    public void setActivityTime(Timestamp activityTime) {
        this.activityTime = activityTime;
    }
    public String getActivityType() {
        return activityType;
    }
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
    public String getActivityDescription() {
        return activityDescription;
    }
    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

}
