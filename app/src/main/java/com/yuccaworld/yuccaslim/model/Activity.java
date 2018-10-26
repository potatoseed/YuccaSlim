package com.yuccaworld.yuccaslim.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Activity")
public class Activity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String activityID;
    private String userID;
    private String userEmail;
    private int activityTypeID;
    private String activityTypeDesc;
    private long activityTime;
    private int foodID;
    private String foodName;
    private int valueInt;
    private float valueDecimal;
    private String valueText;
    private int hintID;
    private String hint;
    private int ind1;
    private int ind2;
    private String activityDate;
    private Date updateTime;

    public Activity(int id, String activityID, String userID, String userEmail, int activityTypeID, String activityTypeDesc, long activityTime, int foodID, String foodName, int valueInt, float valueDecimal, String valueText, int hintID, String hint, int ind1, int ind2, String activityDate, Date updateTime) {
        this.id = id;
        this.activityID = activityID;
        this.userID = userID;
        this.userEmail = userEmail;
        this.activityTypeID = activityTypeID;
        this.activityTypeDesc = activityTypeDesc;
        this.activityTime = activityTime;
        this.foodID = foodID;
        this.foodName = foodName;
        this.valueInt = valueInt;
        this.valueDecimal = valueDecimal;
        this.valueText = valueText;
        this.hintID = hintID;
        this.hint = hint;
        this.ind1 = ind1;
        this.ind2 = ind2;
        this.activityDate = activityDate;
        this.updateTime = updateTime;
    }

    @Ignore
    public Activity() {
    }

    @Ignore
    public Activity(String activityID, String userID, String userEmail, int activityTypeID, String activityTypeDesc, long activityTime, int foodID, String foodName, int valueInt, float valueDecimal, String valueText, int hintID, String hint, int ind1, int ind2, String activityDate, Date updateTime) {
        this.activityID = activityID;
        this.userID = userID;
        this.userEmail = userEmail;
        this.activityTypeID = activityTypeID;
        this.activityTypeDesc = activityTypeDesc;
        this.activityTime = activityTime;
        this.foodID = foodID;
        this.foodName = foodName;
        this.valueInt = valueInt;
        this.valueDecimal = valueDecimal;
        this.valueText = valueText;
        this.hintID = hintID;
        this.hint = hint;
        this.ind1 = ind1;
        this.ind2 = ind2;
        this.activityDate = activityDate;
        this.updateTime = updateTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getActivityTypeDesc() {
        return activityTypeDesc;
    }

    public void setActivityTypeDesc(String activityTypeDesc) {
        this.activityTypeDesc = activityTypeDesc;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
}
