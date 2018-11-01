package com.yuccaworld.yuccaslim;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.yuccaworld.yuccaslim.data.AppDatabase;
import com.yuccaworld.yuccaslim.data.SlimRepository;
import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.model.Daily;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TodayViewModel extends AndroidViewModel {
    private LiveData<List<Activity>> mActivityList;
    private LiveData<Daily> mDaily;
    private AppDatabase mDatabase;
    public Daily daily;
    public TodayViewModel(@NonNull Application application) {
        super(application);
//        database = AppDatabase.getInstance(this.getApplication());
//        mActivityList = database.activityDao().loadAllActivity();
        mDatabase = AppDatabase.getInstance(this.getApplication());
        mActivityList = mDatabase.activityDao().loadActivityToday();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        mDaily = mDatabase.dailyDao().loadDailyByDate(currentDate);
        daily = new Daily(currentDate,SlimUtils.gUid,0,150,200,new Date());
    }

    public LiveData<List<com.yuccaworld.yuccaslim.model.Activity>> getTodayActivityList() {
        return mActivityList;
    }

    public LiveData<Daily> getTodayDaily() {
        return mDaily;
    }

    public LiveData<List<com.yuccaworld.yuccaslim.model.Activity>> getActivityListHistory(int hoursFromNow) {
        return mDatabase.activityDao().loadActivityByHours(hoursFromNow);
    }

}
