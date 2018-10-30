package com.yuccaworld.yuccaslim.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.yuccaworld.yuccaslim.model.Food;

import java.util.List;

public class FoodViewModel extends AndroidViewModel {
    private LiveData<List<Food>> foodList;
    public FoodViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        foodList = database.FoodDao().loadAllFood();
    }

    public LiveData<List<Food>> getFoodList(){
        return foodList;
    }
}
