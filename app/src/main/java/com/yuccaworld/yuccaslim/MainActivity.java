package com.yuccaworld.yuccaslim;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.data.SlimDBHelper;

public class MainActivity extends AppCompatActivity {

    // Create a local field member of type SQLiteDatabase called mDb
    private SQLiteDatabase mDB;
    private SlimDBHelper mSlimDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create a SlimDbHelper instance, pass "this" to the constructor as context
        mSlimDBHelper = new SlimDBHelper(this);

        // Set the gender switch only male or female
        final Switch switchMale = (Switch)findViewById(R.id.switchMale);
        final Switch switchFemale = (Switch)findViewById(R.id.switchFemale);
        switchMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.click_message, Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                if (switchMale.isChecked()){
                    switchFemale.setChecked(false);
                }

            }
        });
        switchFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchFemale.isChecked()){
                    switchMale.setChecked(false);
                }
            }
        });

/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // collect values from UI
                ContentValues cv = new ContentValues();
                EditText et = (EditText) findViewById(R.id.editTextName);
                cv.put(SlimContract.SlimDB.COLUMN_USER_NAME, et.getText().toString().trim());
                et = (EditText) findViewById(R.id.editTextMail);
                cv.put(SlimContract.SlimDB.COLUMN_EMAIL, et.getText().toString().trim());
                et = (EditText) findViewById(R.id.editTextAge);
                Double d = Double.parseDouble(et.getText().toString().trim());
                cv.put(SlimContract.SlimDB.COLUMN_AGE, d);
                et = (EditText) findViewById(R.id.editTextWeight);
                cv.put(SlimContract.SlimDB.COLUMN_WEIGHT, Double.parseDouble(et.getText().toString().trim()));
                Switch s = (Switch) findViewById(R.id.switchMale);
                if (s.isChecked()) {
                    cv.put(SlimContract.SlimDB.COLUMN_GENDER, "M");
                } else {
                    s = (Switch) findViewById(R.id.switchFemale);
                    if (s.isChecked()) {
                        cv.put(SlimContract.SlimDB.COLUMN_GENDER, "F");
                    } else {
                        cv.put(SlimContract.SlimDB.COLUMN_GENDER, "U");
                    }
                }
                ;
                insertDB(cv);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
            newRowID = mDB.insertOrThrow(SlimContract.SlimDB.TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.e("Insert fail return code " + String.valueOf(newRowID) + " error message", e.toString());
        }
        displayDBInfo(mDB);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Cursor getAllUsers(){
        return mDB.query(SlimContract.SlimDB.TABLE_NAME,null,null,null,null,null,null);
    }

    private void ShowUsers() {
        if (mDB == null) {
            mDB = mSlimDBHelper.getReadableDatabase();
        }
        ;
        Cursor cursor = null;
        //Cursor cursor = mDB.query(SlimContract.SlimDB.TABLE_NAME,null,null,null,null,null,null);
        try {
            cursor = mDB.rawQuery("SELECT * FROM " + SlimContract.SlimDB.TABLE_NAME, null);
        } catch (SQLiteException e) {
            Log.e("Select fail ", " Error Message:" + e.toString());
        }

        TextView textView = (TextView) findViewById(R.id.textViewLogo);
        textView.setText("Show Users" + "\n");
        while (cursor.moveToNext()){
            int nameColumnIndex = cursor.getColumnIndex(SlimContract.SlimDB.COLUMN_USER_NAME);
            String name = cursor.getString(nameColumnIndex);
            textView.append("Name:" + name + "\n");
        }
    }

    public void displayDBInfo(SQLiteDatabase db) {
        if (db == null) {
            return;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + SlimContract.SlimDB.TABLE_NAME, null);
        try {
            TextView view = (TextView) findViewById(R.id.textViewLogo);
            view.append(" No of rows = " + cursor.getCount());
        } finally {
            cursor.close();
        }
    }
}
