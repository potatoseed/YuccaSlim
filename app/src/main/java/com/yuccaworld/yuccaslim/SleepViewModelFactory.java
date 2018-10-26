package com.yuccaworld.yuccaslim;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.yuccaworld.yuccaslim.data.AppDatabase;

public class SleepViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mDb;
    private final String mActivityId;

    public SleepViewModelFactory(AppDatabase mDb, String activityId) {
        this.mDb = mDb;
        this.mActivityId = activityId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new SleepViewModel(mDb, mActivityId);
    }
}
