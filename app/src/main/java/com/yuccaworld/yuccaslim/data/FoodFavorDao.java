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
public interface FoodFavorDao {
    @Query("SELECT * FROM FoodFavor ORDER BY sortOrder")
    LiveData<List<FoodFavor>> loadAllFoodFavor();

    @Query("SELECT id, foodID, FoodName FROM FoodFavor ORDER BY sortOrder")
    List<Food> loadFoodFavorAsFood();


    @Insert
    long insertFoodFavor(FoodFavor foodFavor);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int UpdateFoodFavorr(FoodFavor foodFavor);

    @Delete
    void deleteFoodFavor(FoodFavor foodFavor);

    @Query("SELECT * FROM FoodFavor WHERE foodId = :foodId")
    FoodFavor loadFoodFavorById(int foodId);

    @Query("UPDATE FoodFavor SET sortOrder = sortOrder + 1, updateTime = 'now' WHERE foodId = :foodId")
    int addFoodFavorCountById(int foodId);

    @Query("UPDATE FoodFavor SET sortOrder = sortOrder - 1, updateTime = 'now'  WHERE foodId = :foodId")
    int lessFoodFavorCountById(int foodId);
}
