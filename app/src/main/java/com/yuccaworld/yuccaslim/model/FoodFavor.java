package com.yuccaworld.yuccaslim.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "FoodFavor")
public class FoodFavor {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") private int id;
    @ColumnInfo(name = "foodId") private int foodId;
    @ColumnInfo(name = "foodName") private String foodName;
    @ColumnInfo(name = "foodQty") private float foodQty;
    @ColumnInfo(name = "sortOrder") private int sortOrder;
    @ColumnInfo(name = "updateTime") private Date updateTime;

    public FoodFavor(int id, int foodId, String foodName, float foodQty, int sortOrder, Date updateTime) {
        this.id = id;
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodQty = foodQty;
        this.sortOrder = sortOrder;
        this.updateTime = updateTime;
    }

    @Ignore
    public FoodFavor(int foodId, String foodName, float foodQty, int sortOrder, Date updateTime) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodQty = foodQty;
        this.sortOrder = sortOrder;
        this.updateTime = updateTime;
    }

    @Ignore
    public FoodFavor() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public float getFoodQty() {
        return foodQty;
    }

    public void setFoodQty(float foodQty) {
        this.foodQty = foodQty;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
