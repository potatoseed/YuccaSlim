package com.yuccaworld.yuccaslim.data;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yuccaworld.yuccaslim.model.Daily;

import java.util.List;

@Dao
public interface DailyDao {

    @Query("SELECT * FROM Daily ORDER BY date")
    LiveData<List<Daily>> loadAllDaily();

    @Insert
    void insertDaily(Daily daily);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDaily(Daily daily);

    @Delete
    void deleteDaily(Daily daily);

    @Query("SELECT * FROM Daily WHERE date = :date")
    LiveData<Daily> loadDailyByDate(String date);
}
