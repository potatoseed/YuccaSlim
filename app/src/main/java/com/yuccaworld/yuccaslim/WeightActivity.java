package com.yuccaworld.yuccaslim;

import android.content.Intent;
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
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.model.ActivityInfo;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class WeightActivity extends AppActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @DecimalMin(value = 10, sequence = 1, messageResId = R.string.min_weight_validation)
    @Or
    @DecimalMax(value = 300, sequence = 2, messageResId = R.string.max_weight_validation)
    private EditText weightInput;

    private String mMode = "";
    private static Uri mUri;
    private static final int ID_WEIGHT_LOADER = 101;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mActivityID ="";
    private DatabaseReference mFirebaseDB = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);
        Intent intent = getIntent();
        SlimUtils.gUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SlimUtils.gUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            mMode = intent.getStringExtra(Intent.EXTRA_TEXT);
            mActivityID = intent.getStringExtra(Intent.EXTRA_UID);
        }
        Button button = (Button) findViewById(R.id.buttonAdd);
        weightInput = (EditText) findViewById(R.id.editTextWeightInput);
        if ("EDIT".equals(mMode)) {
            mUri = getIntent().getData();
            // if no Uri data in the intent, add new weight, no need to load data.

            LoaderManager loaderManager = getSupportLoaderManager();
            if (loaderManager == null){
                loaderManager.initLoader(ID_WEIGHT_LOADER, null, this);
            } else {
                loaderManager.restartLoader(ID_WEIGHT_LOADER, null, this);
            }
            button.setText(R.string.button_label_change);
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

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SlimContract.SlimDB.COLUMN_USER_EMAIL, SlimUtils.gUid);
                    // Activity type id=1 for weight measure
                    contentValues.put(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_ID, 1);
                    contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_TIME, weightTime.getTimeInMillis());
                    contentValues.put(SlimContract.SlimDB.COLUMN_FOOD_ID, 0);
                    contentValues.put(SlimContract.SlimDB.COLUMN_VALUE_INT, 0);
                    contentValues.put(SlimContract.SlimDB.COLUMN_VALUE_DECIMAL, weight);
                    contentValues.put(SlimContract.SlimDB.COLUMN_VALUE_TEXT, "");
                    contentValues.put(SlimContract.SlimDB.COLUMN_IND1, 0);
                    contentValues.put(SlimContract.SlimDB.COLUMN_IND2, 0);
                    // Hint ID update from cloud
                    contentValues.put(SlimContract.SlimDB.COLUMN_HINT_ID, 0);


                    // Insert in Sqlite DB and Upload to firebase realtime DB
                    Uri uri = null;
                    int updatedRow = 0;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH：mm：ss");
                    String currentDateandTime = sdf.format(new Date());
                    if ("EDIT".equals(mMode)) {
                        contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_ID, mActivityID);

                        ActivityInfo activityInfo = new ActivityInfo(mActivityID,SlimUtils.gUid,SlimUtils.gUserEmail,1,
                                weightTime.getTimeInMillis(),0, 0,weight,"",0,"",0,0,currentDateandTime);
                        updatedRow = getContentResolver().update(mUri,contentValues,null,null);
                        if (updatedRow != 0) {
                            mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(mActivityID).setValue(activityInfo);
                        }
                    } else {
                        String uid = UUID.randomUUID().toString();
                        contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_ID, uid);
                        uri = getContentResolver().insert(SlimContract.SlimDB.CONTENT_ACTIVITY_URI, contentValues);
                        if (uri != null) {
                            ActivityInfo activityInfo = new ActivityInfo(uid,SlimUtils.gUid,SlimUtils.gUserEmail,1,
                                    weightTime.getTimeInMillis(),0, 0,weight,"",0,"",0,0,currentDateandTime);
                            mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(uid).setValue(activityInfo);
                            Snackbar.make(view, "uri : " + uri, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        } else {
                            Snackbar.make(view, "uri is null" + uri, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }
                    finish();
                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }

    private static class WeightAsyncTaskLoader extends AsyncTaskLoader<Cursor> {
        private WeakReference<WeightActivity> activityWeakReference;

        public WeightAsyncTaskLoader(WeightActivity context) {
            super(context);
            activityWeakReference = new WeakReference<WeightActivity>(context);
        }

        @Override
        public Cursor loadInBackground() {
            // get a reference to the activity if it is still there
            WeightActivity activity = activityWeakReference.get();
            try {
                Cursor cursor = activity.getContentResolver().query(mUri,null,null,null,null);
                return cursor;
            }catch (Exception e) {
                Log.e(TAG, "Failed to asynchronously load data.");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_WEIGHT_LOADER:
                return new WeightAsyncTaskLoader(this);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int activityValueDecimalIndex = data.getColumnIndex(SlimContract.SlimDB.COLUMN_VALUE_DECIMAL);
        EditText editTextWeight = (EditText) findViewById(R.id.editTextWeightInput);
        if (data.moveToFirst()) {
            float weight = data.getFloat(activityValueDecimalIndex);
            editTextWeight.setText(Float.toString(weight));

            // retrieve the time from database and Set the timepicker to that time
            long l = data.getLong(data.getColumnIndex(SlimContract.SlimDB.COLUMN_ACTIVITY_TIME));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(l);
            TimePicker timePicker = (TimePicker) findViewById(R.id.timePickerWeightTime);
            timePicker.setCurrentHour(c.get(c.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(c.MINUTE));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
