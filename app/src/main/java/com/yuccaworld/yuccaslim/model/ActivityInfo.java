package com.yuccaworld.yuccaslim.model;

import java.util.Date;

/**
 * Created by Yung on 9/3/2017.
 */

public class ActivityInfo {
    //private String activityID;
    private String activityID;
    private String UserID;
    private String UserEmail;
    private int activityTypeID;
    private long activityTime;
    private int foodID;
    private int valueInt;
    private float valueDecimal;
    private String valueText;
    private int hintID;
    private String hint;
    private int ind1;
    private int ind2;
    private String updateDate;

    public ActivityInfo(String activityID, String userID, String userEmail, int activityTypeID, long activityTime, int foodID, int valueInt, float valueDecimal, String valueText, int hintID, String hint, int ind1, int ind2, String updateDate) {
        this.activityID = activityID;
        UserID = userID;
        UserEmail = userEmail;
        this.activityTypeID = activityTypeID;
        this.activityTime = activityTime;
        this.foodID = foodID;
        this.valueInt = valueInt;
        this.valueDecimal = valueDecimal;
        this.valueText = valueText;
        this.hintID = hintID;
        this.hint = hint;
        this.ind1 = ind1;
        this.ind2 = ind2;
        this.updateDate = updateDate;
    }

    public ActivityInfo() {
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public int getActivityTypeID() {
        return activityTypeID;
    }

    public void setActivityTypeID(int activityTypeID) {
        this.activityTypeID = activityTypeID;
    }

    public long getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(long activityTime) {
        this.activityTime = activityTime;
    }

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public int getValueInt() {
        return valueInt;
    }

    public void setValueInt(int valueInt) {
        this.valueInt = valueInt;
    }

    public float getValueDecimal() {
        return valueDecimal;
    }

    public void setValueDecimal(float valueDecimal) {
        this.valueDecimal = valueDecimal;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public int getHintID() {
        return hintID;
    }

    public void setHintID(int hintID) {
        this.hintID = hintID;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getInd1() {
        return ind1;
    }

    public void setInd1(int ind1) {
        this.ind1 = ind1;
    }

    public int getInd2() {
        return ind2;
    }

    public void setInd2(int ind2) {
        this.ind2 = ind2;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
