package com.yuccaworld.yuccaslim.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Entity(tableName = "Daily")
public class Daily {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String userID;
    private int slimScore;
    private int targetFat;
    private int targetHeavy;
    @Nullable
    private int hintId1;
    private String hint1 = "";
    private Date updateTime;

//    public Daily(int id, String date, String userID, int slimScore, int targetFat, int targetHeavy, String hint1, Date updateTime) {
//        this.id = id;
//        this.date = date;
//        this.userID = userID;
//        this.slimScore = slimScore;
//        this.targetFat = targetFat;
//        this.targetHeavy = targetHeavy;
//        this.hint1 = hint1;
//        this.updateTime = updateTime;
//    }


    public Daily(int id, String date, String userID, int slimScore, int targetFat, int targetHeavy, int hintId1, String hint1, Date updateTime) {
        this.id = id;
        this.date = date;
        this.userID = userID;
        this.slimScore = slimScore;
        this.targetFat = targetFat;
        this.targetHeavy = targetHeavy;
        this.hintId1 = hintId1;
        this.hint1 = hint1;
        this.updateTime = updateTime;
    }

    @Ignore
    public Daily(String date, String userID, int slimScore, int targetFat, int targetHeavy, int hintId1, String hint1, Date updateTime) {
        this.date = date;
        this.userID = userID;
        this.slimScore = slimScore;
        this.targetFat = targetFat;
        this.targetHeavy = targetHeavy;
        this.hintId1 = hintId1;
        this.hint1 = hint1;
        this.updateTime = updateTime;
    }

    public Daily() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getSlimScore() {
        return slimScore;
    }

    public void setSlimScore(int slimScore) {
        this.slimScore = slimScore;
    }

    public int getTargetFat() {
        return targetFat;
    }

    public void setTargetFat(int targetFat) {
        this.targetFat = targetFat;
    }

    public int getTargetHeavy() {
        return targetHeavy;
    }

    public void setTargetHeavy(int targetHeavy) {
        this.targetHeavy = targetHeavy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHint1() {
        return hint1;
    }

    public void setHint1(String hint1) {
        this.hint1 = hint1;
    }

    public int getHintId1() {
        return hintId1;
    }

    public void setHintId1(int hintId1) {
        this.hintId1 = hintId1;
    }
}
