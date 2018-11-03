package com.yuccaworld.yuccaslim;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.yuccaworld.yuccaslim.data.AppDatabase;
import com.yuccaworld.yuccaslim.model.FoodFavor;

import java.util.List;

public class FoodViewModel extends AndroidViewModel {
    private LiveData<List<FoodFavor>> foodList;
    private LiveData<List<FoodFavor>> foodFavorList;
    public FoodViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        foodList = database.FoodDao().loadAllFoodToFoodFavorLive();
        foodFavorList = database.FoodFavorDao().loadAllFoodFavorLive();
    }

    public LiveData<List<FoodFavor>> getFoodList(){
        return foodList;
    }

    public LiveData<List<FoodFavor>> getFoodFavorList(){
        return foodFavorList;
    }
}
