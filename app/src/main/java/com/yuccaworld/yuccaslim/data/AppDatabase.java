package com.yuccaworld.yuccaslim.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

import com.huma.room_for_asset.RoomAsset;
import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.model.Daily;
import com.yuccaworld.yuccaslim.model.Food;
import com.yuccaworld.yuccaslim.model.FoodFavor;


@Database(entities = {Daily.class, Activity.class, Food.class, FoodFavor.class}, version = 2, exportSchema = false)
//@Database(entities = {Daily.class, Activity.class, }, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "YSDB.db";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
//                sInstance = Room.databaseBuilder(context.getApplicationContext(),
//                        AppDatabase.class, AppDatabase.DATABASE_NAME)
//                        .addMigrations(AppDatabase.MIGRATION_1_2)
//                        .allowMainThreadQueries()
//                        .fallbackToDestructiveMigration()
//                        .build();
                sInstance = RoomAsset.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .allowMainThreadQueries()
//                        .addMigrations(AppDatabase.MIGRATION_2_3)
//                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE `FoodFavor` (`id` INTEGER NOT NULL, `foodId` INTEGER NOT NULL, `foodName` TEXT, `foodQty` REAL NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        }
    };
    public abstract DailyDao dailyDao();
    public abstract ActivityDao activityDao();
    public abstract FoodDao FoodDao();
    public abstract FoodFavorDao FoodFavorDao();
}
