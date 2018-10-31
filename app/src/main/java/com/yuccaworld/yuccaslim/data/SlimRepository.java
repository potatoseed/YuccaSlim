package com.yuccaworld.yuccaslim.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.utilities.AppExecutors;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.util.List;


public class SlimRepository {
    private static final String LOG_TAG = SlimRepository.class.getSimpleName();

    // For Singleton Instantiation
    private static final Object LOCK = new Object();
    private static SlimRepository sInstance;
    private AppDatabase mDatabase;
    private DatabaseReference mFirebaseDB = FirebaseDatabase.getInstance().getReference();;
    private AppExecutors mExecutors;
    private boolean mInitialized = false;


    public SlimRepository(AppDatabase database) {
        this.mDatabase = database;
        // Get data from firebase Activity Node
    }

    public synchronized static SlimRepository getsInstance(AppDatabase database) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new SlimRepository(database);
            }
        }
        return sInstance;
    }

}

