package com.yuccaworld.yuccaslim.model;

public class Food {
    private int foodID;
    private String foodDesc;

    public Food(int foodID, String foodDesc) {
        this.foodID = foodID;
        this.foodDesc = foodDesc;
    }

    public int getFoodID() {
        return foodID;
    }
    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public void setFoodDesc(String foodDesc) {
        this.foodDesc = foodDesc;
    }


}
