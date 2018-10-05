package com.yuccaworld.yuccaslim;

import android.content.ContentValues;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.util.Calendar;
import java.util.UUID;

public class SleepActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    private String mMode = "";
    private static Uri mUri;
    private static final int ID_SLEEP = 130;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            mMode = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
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

                UUID uuid = UUID.randomUUID();
                byte[] bytes = SlimUtils.toByte(uuid);
                contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_ID, bytes);

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
                    sleepORWake = getString(R.string.text_wakeup);
                else sleepORWake = getString(R.string.text_sleep);
                contentValues.put(SlimContract.SlimDB.COLUMN_VALUE_TEXT, sleepORWake);

                // TODO Fill in Hint ID by other logic later
                contentValues.put(SlimContract.SlimDB.COLUMN_HINT_ID, 1);
                Uri uri = null;
                int updatedRow = 0;
                if ("EDIT".equals(mMode)) {
                    updatedRow = getContentResolver().update(mUri,contentValues,null,null);
                } else {
                    uri = getContentResolver().insert(SlimContract.SlimDB.CONTENT_ACTIVITY_URI, contentValues);
                    if (uri != null) {
                        Snackbar.make(view, "uri : " + uri, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "uri is null" + uri, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
                finish();
            }
        });
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }
}
