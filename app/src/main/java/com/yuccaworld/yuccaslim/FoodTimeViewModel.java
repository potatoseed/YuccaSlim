package com.yuccaworld.yuccaslim;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.yuccaworld.yuccaslim.data.AppDatabase;
import com.yuccaworld.yuccaslim.model.Activity;

public class FoodTimeViewModel extends ViewModel {
    private LiveData<Activity> activityLiveData;

    public FoodTimeViewModel(AppDatabase database, String activityId) {
        activityLiveData = database.activityDao().loadActivityByActivityId(activityId);
    }

    public LiveData<Activity> getActivityLiveData() {
        return activityLiveData;
    }
}
