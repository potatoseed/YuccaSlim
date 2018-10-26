package com.yuccaworld.yuccaslim;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.yuccaworld.yuccaslim.data.AppDatabase;

public class WeightViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mDb;
    private final String mActivityId;

    public WeightViewModelFactory(AppDatabase mDb, String activityId) {
        this.mDb = mDb;
        this.mActivityId = activityId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new WeightViewModel(mDb, mActivityId);
    }
}
