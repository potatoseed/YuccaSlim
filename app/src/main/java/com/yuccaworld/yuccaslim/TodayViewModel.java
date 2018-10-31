package com.yuccaworld.yuccaslim;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import com.yuccaworld.yuccaslim.data.AppDatabase;
import com.yuccaworld.yuccaslim.data.SlimRepository;
import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.utilities.AppExecutors;

import java.util.List;

public class TodayViewModel extends AndroidViewModel {
    private LiveData<List<Activity>> activityList;
    @NonNull
    private SlimRepository repo;
    private AppDatabase database;
    public TodayViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(this.getApplication());
        activityList = database.activityDao().loadAllActivity();
//        repo = SlimRepository.getsInstance(database);
//        repo.loadActivity(30);
//        activityList = repo.getActivityList();
    }

    public LiveData<List<com.yuccaworld.yuccaslim.model.Activity>> getActivityList() {
//        repo = SlimRepository.getsInstance(database);
//        repo.loadActivity(30);
//        activityList = repo.getActivityList();
//        activityList = SlimRepository.getsInstance(database).getActivityList();
        return activityList;
    }

    public LiveData<List<com.yuccaworld.yuccaslim.model.Activity>> getActivityListHistory(int hoursFromNow) {
        return database.activityDao().loadActivityByHours(hoursFromNow);
    }
}
