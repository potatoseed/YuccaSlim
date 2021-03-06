package com.yuccaworld.yuccaslim.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.yuccaworld.yuccaslim.model.Food;
import com.yuccaworld.yuccaslim.model.FoodFavor;

import java.util.List;

@Dao
public interface FoodDao {
    @Query("SELECT * FROM Food ORDER BY foodId")
    LiveData<List<Food>> loadAllFood();

    @Query("SELECT id, foodID, FoodName, 100 AS foodQty, 10000 AS sortOrder, 1092941466 AS updateTime  FROM FOOD ORDER BY foodId")
    LiveData<List<FoodFavor>> loadAllFoodToFoodFavorLive();

    @Query("SELECT id, foodID, FoodName, 100 AS foodQty, 10000 AS sortOrder, 1092941466 AS updateTime  FROM FOOD ORDER BY foodId")
    List<FoodFavor> loadAllFoodToFoodFavor();

    @Insert
    long insertFood(Food food);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateFood(Food food);

    @Delete
    void deleteFood(Food food);

    @Query("SELECT * FROM Food WHERE foodId = :foodId")
    LiveData<Food> loadFoodById(int foodId);

//    @Query("SELECT * FROM Food WHERE foodName like :foodName")
    @Query("SELECT id, foodID, FoodName, 100 AS foodQty, 10000 AS sortOrder, 1092941466 AS updateTime FROM Food WHERE foodName like '%' || :foodName || '%'")
    List<FoodFavor> loadFoodLikeName(String foodName);
}
