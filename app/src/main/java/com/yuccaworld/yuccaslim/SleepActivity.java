package com.yuccaworld.yuccaslim;

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
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.model.ActivityInfo;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.UUID;

import static com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.TEXT_VALUE_WAKEUP;

public class SleepActivity extends AppActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mMode = "";
    private static Uri mUri;
    private static final int ID_SLEEP = 130;
    private String mActivityID ="";
    private DatabaseReference mFirebaseDB = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        SlimUtils.gUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SlimUtils.gUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            mMode = intent.getStringExtra(Intent.EXTRA_TEXT);
            mActivityID = intent.getStringExtra(Intent.EXTRA_UID);
        }

        // Change or Add Button for edit or add sleep activity
        Button button = (Button) findViewById(R.id.buttonAddSleep);
        if ("EDIT".equals(mMode)) {
            mUri = getIntent().getData();
            // if no Uri data in the intent, add new weight, no need to load data.
            LoaderManager loaderManager = getSupportLoaderManager();
            if (loaderManager == null){
                loaderManager.initLoader(ID_SLEEP, null, this);
            } else {
                loaderManager.restartLoader(ID_SLEEP, null, this);
            }
            button.setText(R.string.button_label_change);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "button clicked", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                ContentValues contentValues = new ContentValues();

//                UUID uuid = UUID.randomUUID();
//                byte[] bytes = SlimUtils.toByte(uuid);
                String uid = UUID.randomUUID().toString();
                contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_ID, uid);

                // Activity type id=3 for sleep or wake up
                contentValues.put(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_ID, 3);

                // get time from time picker
                TimePicker timePicker = findViewById(R.id.timePickerSleep);
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                Calendar sleepTime = Calendar.getInstance();
                sleepTime.set(Calendar.HOUR_OF_DAY, hour);
                sleepTime.set(Calendar.MINUTE, min);
                contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_TIME, sleepTime.getTimeInMillis());
                ToggleButton toggleButton = findViewById(R.id.toggleButtonSleep);
                String sleepORWake;
                if(toggleButton.isChecked())
                    sleepORWake = SlimContract.SlimDB.TEXT_VALUE_WAKEUP;
                else sleepORWake = SlimContract.SlimDB.TEXT_VALUE_SLEEP;
                contentValues.put(SlimContract.SlimDB.COLUMN_VALUE_TEXT, sleepORWake);

                // TODO Fill in Hint ID by other logic later
                contentValues.put(SlimContract.SlimDB.COLUMN_HINT_ID, 0);

                // Insert in Sqlite DB and Upload to firebase realtime DB
                if (mActivityID == "") {mActivityID = uid.toString();}
                ActivityInfo activityInfo = new ActivityInfo(mActivityID,SlimUtils.gUid,SlimUtils.gUserEmail,3,
                        sleepTime.getTimeInMillis(),0, 0,0,sleepORWake,0,"",0,0);
                Uri uri = null;
                int updatedRow = 0;
                if ("EDIT".equals(mMode)) {
                    updatedRow = getContentResolver().update(mUri,contentValues,null,null);
                    if (updatedRow != 0) {
                        mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(mActivityID).setValue(activityInfo);
                    }
                } else {
                    uri = getContentResolver().insert(SlimContract.SlimDB.CONTENT_ACTIVITY_URI, contentValues);
                    if (uri != null) {
                        mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(uid.toString()).setValue(activityInfo);
                        Snackbar.make(view, "uri : " + uri, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "uri is null" + uri, Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

    private static class sleepAsyncTaskLoader extends AsyncTaskLoader<Cursor>{
        private WeakReference<SleepActivity> activityWeakReference;

        public sleepAsyncTaskLoader(SleepActivity context) {
            super(context);
            activityWeakReference = new WeakReference<SleepActivity>(context);
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable
        @Override
        public Cursor loadInBackground() {
            // get a reference to the activity if it is still there
            SleepActivity activity = activityWeakReference.get();
            try {
                Cursor cursor = activity.getContentResolver().query(mUri,null,null,null,null);
                return cursor;
            }catch (Exception e) {
                Log.e(TAG, "Failed to asynchronously load data.");
                e.printStackTrace();
                return null;
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case ID_SLEEP:
                return new sleepAsyncTaskLoader(this);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ToggleButton toggleButton = findViewById(R.id.toggleButtonSleep);
        ImageView imageView = findViewById(R.id.imageViewSleepIcon);
        if (data.moveToFirst()) {
            String sleepORWakeup = data.getString(data.getColumnIndex(SlimContract.SlimDB.COLUMN_VALUE_TEXT));
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
            long l = data.getLong(data.getColumnIndex(SlimContract.SlimDB.COLUMN_ACTIVITY_TIME));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(l);
            TimePicker timePicker = (TimePicker) findViewById(R.id.timePickerSleep);
            timePicker.setCurrentHour(c.get(c.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(c.MINUTE));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ToggleButton toggleButton = findViewById(R.id.toggleButtonSleep);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        ToggleButton toggleButton = findViewById(R.id.toggleButtonSleep);
    }

}
