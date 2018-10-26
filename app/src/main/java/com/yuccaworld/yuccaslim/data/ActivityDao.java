package com.yuccaworld.yuccaslim.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.model.Daily;

import java.util.List;
@Dao
public interface ActivityDao {

    @Query("SELECT * FROM Activity ORDER BY activityTime")
    LiveData<List<Activity>> loadAllActivity();

    @Insert
    long insertActivity(Activity activity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateActivity(Activity activity);

    @Delete
    void deleteActivity(Activity activity);

    @Query("SELECT * FROM Activity WHERE id = :id")
    LiveData<Activity> loadActivityById(int id);

    @Query("SELECT * FROM Activity WHERE activityID = :activityID")
    LiveData<Activity> loadActivityByActivityId(String activityID);
}
