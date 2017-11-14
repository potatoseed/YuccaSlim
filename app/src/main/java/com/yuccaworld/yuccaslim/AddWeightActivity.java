package com.yuccaworld.yuccaslim;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.mobsandgeeks.saripaar.annotation.DecimalMax;
import com.mobsandgeeks.saripaar.annotation.DecimalMin;
import com.mobsandgeeks.saripaar.annotation.Or;
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.UUID;

public class AddWeightActivity extends AppActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @DecimalMin(value = 10, sequence = 1, messageResId = R.string.min_weight_validation)
    @Or
    @DecimalMax(value = 300, sequence = 2, messageResId = R.string.max_weight_validation)
    private EditText weightInput;

    private static Uri mUri;
    private static final int ID_WEIGHT_LOADER = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        initView();
        Button button = (Button) findViewById(R.id.buttonAdd);
        getSupportLoaderManager().initLoader(ID_WEIGHT_LOADER, null, this);
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
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SlimContract.SlimDB.COLUMN_VALUE_DECIMAL, weight);

                    UUID uuid = UUID.randomUUID();
                    byte[] bytes = SlimUtils.toByte(uuid);
                    contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_ID, bytes);

                    // Activity type id=1 for weight measure
                    contentValues.put(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_ID, 1);

                    // get time from time picker
                    TimePicker timePicker = (TimePicker) findViewById(R.id.timePickerWeightTime);
                    int hour = timePicker.getCurrentHour();
                    int min = timePicker.getCurrentMinute();
                    Calendar weightTime = Calendar.getInstance();
                    weightTime.set(Calendar.HOUR_OF_DAY, hour);
                    weightTime.set(Calendar.MINUTE, min);
                    contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_TIME, weightTime.getTimeInMillis());
 

                    // TODO Fill in Hint ID by other logic later
                    contentValues.put(SlimContract.SlimDB.COLUMN_HINT_ID, 1);

                    Uri uri = getContentResolver().insert(SlimContract.SlimDB.CONTENT_ACTIVITY_URI, contentValues);
                    if (uri != null) {
                        Snackbar.make(view, "uri : " + uri, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "uri is null" + uri, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                    finish();
                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }

    private void initView() {
        weightInput = (EditText) findViewById(R.id.editTextWeightInput);
        mUri = getIntent().getData();
        // TODO Continue here
        if (mUri == null) {}
    }

    private static class WeightAsyncTaskLoader extends AsyncTaskLoader<Cursor> {
        private final WeakReference<AddWeightActivity> activityWeakReference;
        private Context mContext;

        public WeightAsyncTaskLoader(AddWeightActivity context) {
            super(context);
            activityWeakReference = new WeakReference<AddWeightActivity>(context);
            mContext = context;
        }

        @Override
        public Cursor loadInBackground() {
            /*
            try {
                Cursor cursor = mContext.getContentResolver().query(mUri,)
            }
            */
            return null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_WEIGHT_LOADER:
                //return new CursorLoader(this, mUri,null,null,null,null);
                return null;
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
