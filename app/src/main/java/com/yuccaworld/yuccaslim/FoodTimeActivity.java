package com.yuccaworld.yuccaslim;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yuccaworld.yuccaslim.data.AppDatabase;
import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.utilities.AppExecutors;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FoodTimeActivity extends AppCompatActivity {
    private String mMode = "";
    public static final String EXTRA_ROW_ID = "ExtraRowID";
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mActivityID ="";
    private DatabaseReference mFirebaseDB = FirebaseDatabase.getInstance().getReference();
    private AppDatabase mDb;
    private int mRowID=-1;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_time);

        mDb = AppDatabase.getInstance(getApplicationContext());
        Intent intent = getIntent();
        SlimUtils.gUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SlimUtils.gUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Button button = findViewById(R.id.buttonFoodTime);
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            mMode = intent.getStringExtra(Intent.EXTRA_TEXT);
            mActivityID = intent.getStringExtra(TodayActivity.EXTRA_ACTIVITY_ID);
            mRowID = intent.getIntExtra(WeightActivity.EXTRA_ROW_ID, 0);
        }
        FoodTimeViewModelFactory factory = new FoodTimeViewModelFactory(mDb, mActivityID);
        final FoodTimeViewModel viewModel = ViewModelProviders.of(this, factory).get(FoodTimeViewModel.class);
        viewModel.getActivityLiveData().observe(this, new Observer<Activity>() {
            @Override
            public void onChanged(@Nullable Activity activity) {
                viewModel.getActivityLiveData().removeObserver(this);
                mActivity = activity;
                populateUI(activity);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get time from time picker
                TimePicker timePicker = (TimePicker) findViewById(R.id.timePickerFoodTime);
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                Calendar weightTime = Calendar.getInstance();
                weightTime.setTimeInMillis(mActivity.getActivityTime());
                weightTime.set(Calendar.HOUR_OF_DAY, hour);
                weightTime.set(Calendar.MINUTE, min);
                final long activityTime = weightTime.getTimeInMillis();

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Update only
                        mActivity.setActivityTime(activityTime);
                        int i = mDb.activityDao().updateActivity(mActivity);
                        if (i > 0) {
                            mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(mActivityID).setValue(mActivity);
                        }
                    }
                });
                finish();
            }
        });

    }

    private void populateUI(Activity activity) {
        if (activity == null) {
            return;
        }
        TextView foodNameTextView = findViewById(R.id.textViewFoodTimeFoodName);
        foodNameTextView.setText(activity.getFoodName());
        long l = activity.getActivityTime();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(l);
        TimePicker timePicker = findViewById(R.id.timePickerFoodTime);
        timePicker.setCurrentHour(c.get(c.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c.get(c.MINUTE));
    }
}
