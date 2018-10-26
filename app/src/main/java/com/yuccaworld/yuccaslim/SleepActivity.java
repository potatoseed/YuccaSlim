package com.yuccaworld.yuccaslim;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yuccaworld.yuccaslim.data.AppDatabase;
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.model.ActivityInfo;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.TEXT_VALUE_WAKEUP;

public class SleepActivity extends AppActivity {
    private String mMode = "";
    private static Uri mUri;
    private String mActivityID ="";
    private DatabaseReference mFirebaseDB = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = MainActivity.class.getSimpleName();
    private AppDatabase mDb;
    private int mRowID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        mDb = AppDatabase.getInstance(getApplicationContext());
        SlimUtils.gUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SlimUtils.gUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Intent intent = getIntent();
        Button button = findViewById(R.id.buttonAddSleep);
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            mMode = intent.getStringExtra(Intent.EXTRA_TEXT);
            mActivityID = intent.getStringExtra(TodayActivity.EXTRA_ACTIVITY_ID);
            mRowID = intent.getIntExtra(WeightActivity.EXTRA_ROW_ID, 0);
            // Change or Add Button for edit or add sleep activity
            button.setText(R.string.button_label_change);
        }

        if ("EDIT".equals(mMode)) {
            SleepViewModelFactory factory = new SleepViewModelFactory(mDb, mActivityID);
            final SleepViewModel viewModel = ViewModelProviders.of(this,factory).get(SleepViewModel.class);
            viewModel.getActivityLiveData().observe(this, new Observer<Activity>() {
                @Override
                public void onChanged(@Nullable Activity activity) {
                    viewModel.getActivityLiveData().removeObserver(this);
                    populateUI(activity);
                }
            });
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get time from time picker
                TimePicker timePicker = findViewById(R.id.timePickerSleep);
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                Calendar sleepTime = Calendar.getInstance();
                sleepTime.set(Calendar.HOUR_OF_DAY, hour);
                sleepTime.set(Calendar.MINUTE, min);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH：mm：ss");
                String currentDateandTime = sdf.format(new Date());
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sdf.format(new Date());
                ToggleButton toggleButton = findViewById(R.id.toggleButtonSleep);
                String sleepORWake;
                if(toggleButton.isChecked())
                    sleepORWake = SlimContract.SlimDB.TEXT_VALUE_WAKEUP;
                else sleepORWake = SlimContract.SlimDB.TEXT_VALUE_SLEEP;

                if ("EDIT".equals(mMode)) {
                    Activity activity = new Activity(mRowID,mActivityID,SlimUtils.gUid,SlimUtils.gUserEmail,3,getResources().getString(R.string.activity_type_3),sleepTime.getTimeInMillis(),0,"",0,0,sleepORWake,0,"",0,0,currentDate,new Date());
                    int i = mDb.activityDao().updateActivity(activity);
                    if (i > 0) {
                        mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(mActivityID).setValue(activity);
                    }
                } else {
                    String uid = UUID.randomUUID().toString();
                    Activity activity = new Activity(uid,SlimUtils.gUid,SlimUtils.gUserEmail,3,getResources().getString(R.string.activity_type_3),sleepTime.getTimeInMillis(),0,"",0,0,sleepORWake,0,"",0,0,currentDate,new Date());
                    long l = mDb.activityDao().insertActivity(activity);
                    if (l > 0) {
                        mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(uid).setValue(activity);
                    }
                }
                finish();
            }
        });

        // Toggle button status change trigger the Icon changes
        ToggleButton toggleButton = findViewById(R.id.toggleButtonSleep);
        final ImageView imageView = findViewById(R.id.imageViewSleepIcon);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    imageView.setImageResource(R.drawable.ic_wake_up_48dp);
                } else {
                    imageView.setImageResource(R.drawable.ic_sleep_black_48dp);
                }
            }
        });
    }

    private void populateUI(Activity activity) {
        ToggleButton toggleButton = findViewById(R.id.toggleButtonSleep);
        ImageView imageView = findViewById(R.id.imageViewSleepIcon);
        String sleepORWakeup = activity.getValueText();
        if (sleepORWakeup.equals(TEXT_VALUE_WAKEUP)) {
            toggleButton.setChecked(true);
            imageView.setImageResource(R.drawable.ic_wake_up_48dp);
        }
        else {
            toggleButton.setChecked(false);
            imageView.setImageResource(R.drawable.ic_sleep_black_48dp);
        }
        toggleButton.requestLayout();

        // retrieve the time from database and Set the timepicker to that time
        long l = activity.getActivityTime();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(l);
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePickerSleep);
        timePicker.setCurrentHour(c.get(c.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c.get(c.MINUTE));
    }
}
