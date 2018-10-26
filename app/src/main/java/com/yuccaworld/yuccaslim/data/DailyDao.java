package com.yuccaworld.yuccaslim.data;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yuccaworld.yuccaslim.model.DailyInfo;

import java.util.List;

@Dao
public interface DailyDao {

    @Query("SELECT * FROM Daily ORDER BY date")
    LiveData<List<DailyInfo>> loadAllDaily();

    @Insert
    void insertDaily(DailyInfo dailyInfo);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDaily(DailyInfo dailyInfo);

    @Delete
    void deleteDaily(DailyInfo dailyInfo);

    @Query("SELECT * FROM Daily WHERE date = :date")
    LiveData<DailyInfo> loadDailyByDate(String date);
}
