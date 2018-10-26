package com.yuccaworld.yuccaslim.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "Daily")
public class DailyInfo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String userID;
    private int slimScore;
    private int targetFat;
    private int targetHeavy;
    private Date updateTime;

    public DailyInfo(int id, String date, String userID, int slimScore, int targetFat, int targetHeavy, Date updateTime) {
        this.id = id;
        this.date = date;
        this.userID = userID;
        this.slimScore = slimScore;
        this.targetFat = targetFat;
        this.targetHeavy = targetHeavy;
        this.updateTime = updateTime;
    }
    @Ignore
    public DailyInfo(String date, String userID, int slimScore, int targetFat, int targetHeavy, Date updateTime) {
        this.date = date;
        this.userID = userID;
        this.slimScore = slimScore;
        this.targetFat = targetFat;
        this.targetHeavy = targetHeavy;
        this.updateTime = updateTime;
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
}
