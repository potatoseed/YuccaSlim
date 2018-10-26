package com.yuccaworld.yuccaslim;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobsandgeeks.saripaar.annotation.DecimalMax;
import com.mobsandgeeks.saripaar.annotation.DecimalMin;
import com.mobsandgeeks.saripaar.annotation.Or;
import com.yuccaworld.yuccaslim.data.AppDatabase;
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.model.ActivityInfo;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class WeightActivity extends AppActivity  {

    @DecimalMin(value = 10, sequence = 1, messageResId = R.string.min_weight_validation)
    @Or
    @DecimalMax(value = 300, sequence = 2, messageResId = R.string.max_weight_validation)
    private String mMode = "";
    public static final String EXTRA_ROW_ID = "ExtraRowID";
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mActivityID ="";
    private DatabaseReference mFirebaseDB = FirebaseDatabase.getInstance().getReference();
    private AppDatabase mDb;
    private int mRowID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);
        mDb = AppDatabase.getInstance(getApplicationContext());
        Intent intent = getIntent();
        SlimUtils.gUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SlimUtils.gUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Button button = findViewById(R.id.buttonAdd);
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            mMode = intent.getStringExtra(Intent.EXTRA_TEXT);
            mActivityID = intent.getStringExtra(TodayActivity.EXTRA_ACTIVITY_ID);
            mRowID = intent.getIntExtra(WeightActivity.EXTRA_ROW_ID, 0);
            button.setText(R.string.button_label_change);
        }

        if ("EDIT".equals(mMode)) {
            WeightViewModelFactory factory = new WeightViewModelFactory(mDb, mActivityID);
            final WeightViewModel viewModel = ViewModelProviders.of(this,factory).get(WeightViewModel.class);
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
                validator.validate();
                if (validated) {
                    String input = ((EditText) findViewById(R.id.editTextWeightInput)).getText().toString();
                    float weight = 0;
                    if (input.length() == 0) {
                        return;
                    }
                    try {
                        weight = Float.parseFloat(input);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Snackbar.make(view, "Invalid Number Input", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }

                    // get time from time picker
                    TimePicker timePicker = (TimePicker) findViewById(R.id.timePickerWeightTime);
                    int hour = timePicker.getCurrentHour();
                    int min = timePicker.getCurrentMinute();
                    Calendar weightTime = Calendar.getInstance();
                    weightTime.set(Calendar.HOUR_OF_DAY, hour);
                    weightTime.set(Calendar.MINUTE, min);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH：mm：ss");
                    String currentDateandTime = sdf.format(new Date());
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = sdf.format(new Date());

                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    if ("EDIT".equals(mMode)) {
                        Activity activity = new Activity(mRowID,mActivityID,SlimUtils.gUid,SlimUtils.gUserEmail,1,getResources().getString(R.string.activity_type_1),weightTime.getTimeInMillis(),0,"",0,weight,"",0,"",0,0,currentDate,new Date());
                        int i = mDb.activityDao().updateActivity(activity);
                        if (i > 0) {
                            mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(mActivityID).setValue(activity);
                        }
                    } else {
                        String uid = UUID.randomUUID().toString();
                        Activity activity = new Activity(uid,SlimUtils.gUid,SlimUtils.gUserEmail,1,getResources().getString(R.string.activity_type_1),weightTime.getTimeInMillis(),0,"",0,weight,"",0,"",0,0,currentDate,new Date());
                        long l = mDb.activityDao().insertActivity(activity);
                        if (l > 0) {
                            mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(uid).setValue(activity);
                        }
                    }
                    finish();
                }
            }
        });
    }
    /**
     * populateUI would be called to populate the UI when in update mode
     */
    private void populateUI(Activity activity) {
        if (activity == null) {
            return;
        }
        EditText editTextWeight = (EditText) findViewById(R.id.editTextWeightInput);
        float weight = activity.getValueDecimal();
        editTextWeight.setText(Float.toString(weight));
        long l = activity.getActivityTime();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(l);
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePickerWeightTime);
        timePicker.setCurrentHour(c.get(c.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c.get(c.MINUTE));
    }
}
