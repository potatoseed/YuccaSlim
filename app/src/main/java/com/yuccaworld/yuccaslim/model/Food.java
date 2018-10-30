package com.yuccaworld.yuccaslim.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Food")
public class Food {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int foodId;
    private String foodName;

    public Food(int id, int foodId, String foodName) {
        this.id = id;
        this.foodId = foodId;
        this.foodName = foodName;
    }

    @Ignore
    public Food() {
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
}
