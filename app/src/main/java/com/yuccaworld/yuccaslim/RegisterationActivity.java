package com.yuccaworld.yuccaslim;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.ConfirmEmail;
import com.mobsandgeeks.saripaar.annotation.DecimalMax;
import com.mobsandgeeks.saripaar.annotation.DecimalMin;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.Max;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.Or;
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.data.SlimDBHelper;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.util.UUID;

public class RegisterationActivity extends AppActivity {
    private FloatingActionButton submit;
    // Create a local field member of type SQLiteDatabase called mDb
    private SQLiteDatabase mDB;
    private SlimDBHelper mSlimDBHelper;

    //@NotEmpty(messageResId = R.string.name_validation)
    @Length(max = 100, min = 1, messageResId = R.string.name_length_validation)
    private EditText name;

    @DecimalMin(value = 10, sequence = 1, messageResId = R.string.min_weight_validation)
    @Or
    @DecimalMax(value = 300, sequence = 2, messageResId = R.string.max_weight_validation)
    private EditText weight;

    @Min(value = 1, messageResId = R.string.min_age_validation)
    @Or
    @Max(value = 300, messageResId = R.string.max_age_validation)
    private EditText age;

    @Email(messageResId = R.string.email_validation)
    private EditText email;

    @ConfirmEmail
    private EditText confirmEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        // Create a SlimDbHelper instance, pass "this" to the constructor as context
        mSlimDBHelper = new SlimDBHelper(this);

        // Init validator
        initView();

        // Set the gender switch only male or female
        final Switch switchMale = (Switch) findViewById(R.id.switchMale);
        final Switch switchFemale = (Switch) findViewById(R.id.switchFemale);
        switchMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.click_message, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                if (switchMale.isChecked()) {
                    switchFemale.setChecked(false);
                }

            }
        });
        switchFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchFemale.isChecked()) {
                    switchMale.setChecked(false);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabFinishRegistration);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
                if (!(switchFemale.isChecked() || switchMale.isChecked())) {
                    validated = false;
                    switchMale.setError(getString(R.string.gender_validation));
                    switchFemale.setError(getString(R.string.gender_validation));
                }
                if (validated) {
                    Snackbar.make(view, "Good input", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    // collect values from UI
                    ContentValues cv = new ContentValues();
                    UUID uuid = UUID.randomUUID();
                    byte[] bytes = SlimUtils.toByte(uuid);
                    cv.put(SlimContract.SlimDB.COLUMN_USER_ID, bytes);
                    EditText et = (EditText) findViewById(R.id.editTextName);
                    cv.put(com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.COLUMN_USER_NAME, et.getText().toString().trim());
                    et = (EditText) findViewById(R.id.editTextMail);
                    cv.put(com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.COLUMN_EMAIL, et.getText().toString().trim());
                    et = (EditText) findViewById(R.id.editTextAge);
                    Double d = Double.parseDouble(et.getText().toString().trim());
                    cv.put(com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.COLUMN_AGE, d);
                    et = (EditText) findViewById(R.id.editTextWeight);
                    cv.put(com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.COLUMN_WEIGHT, Double.parseDouble(et.getText().toString().trim()));
                    Switch s = (Switch) findViewById(R.id.switchMale);
                    if (s.isChecked()) {
                        cv.put(com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.COLUMN_GENDER, "M");
                    } else {
                        s = (Switch) findViewById(R.id.switchFemale);
                        if (s.isChecked()) {
                            cv.put(com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.COLUMN_GENDER, "F");
                        } else {
                            cv.put(SlimContract.SlimDB.COLUMN_GENDER, "U");
                        }
                    }
                    ;
                    insertDB(cv);
                }
            }
        });

        // Run the getAllGuests function and store the result in a Cursor variable
        ShowUsers();
        displayDBInfo(mDB);
    }

    private void insertDB(ContentValues cv) {
        mDB = mSlimDBHelper.getWritableDatabase();
        long newRowID = 0;
        //TestUtil.insertFakeData(mDB);
        try {
            newRowID = mDB.insertOrThrow(com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.TABLE_USER, null, cv);
        } catch (SQLException e) {
            Log.e("Insert fail return code " + String.valueOf(newRowID) + " error message", e.toString());
        }
        //displayDBInfo(mDB);
    }

    private Cursor getAllUsers() {
        return mDB.query(com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.TABLE_USER, null, null, null, null, null, null);
    }

    private void ShowUsers() {
        if (mDB == null) {
            mDB = mSlimDBHelper.getReadableDatabase();
        }
        ;
        Cursor cursor = null;
        //Cursor cursor = mDB.query(SlimContract.SlimDB.TABLE_USER,null,null,null,null,null,null);
        try {
            cursor = mDB.rawQuery("SELECT * FROM " + com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.TABLE_USER, null);
        } catch (SQLiteException e) {
            Log.e("Select fail ", " Error Message:" + e.toString());
        }

        TextView textView = (TextView) findViewById(R.id.textViewLogo);
        textView.setText("Show User ID:" + "\n");
        while (cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex(com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.COLUMN_USER_NAME);
            //int userIdColumnIndex = cursor.getColumnIndex(SlimContract.SlimDB.COLUMN_USER_ID);
            //String name = cursor.getString(userIdColumnIndex);
            byte[] ids = cursor.getBlob(cursor.getColumnIndex(SlimContract.SlimDB.COLUMN_USER_ID));
            UUID uuid = SlimUtils.toUUID(ids);
            textView.append("User ID:" + uuid.toString() + "\n");
        }
    }

    public void displayDBInfo(SQLiteDatabase db) {
        if (db == null) {
            return;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.TABLE_USER, null);
        try {
            TextView view = (TextView) findViewById(R.id.textViewLogo);
            view.append(" No of rows = " + cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    private void initView() {
        name = (EditText) findViewById(R.id.editTextName);
        email = (EditText) findViewById(R.id.editTextMail);
        confirmEmail = (EditText) findViewById(R.id.editTextConfrimEmail);
        weight = (EditText) findViewById(R.id.editTextWeight);
        age = (EditText) findViewById(R.id.editTextAge);
        submit = (FloatingActionButton) findViewById(R.id.fabFinishRegistration);
        //submit.setOnClickListener(this);
    }
}
