package com.yuccaworld.yuccaslim;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.data.SlimDBHelper;
import com.yuccaworld.yuccaslim.data.TestUtil;

public class MainActivity extends AppCompatActivity {

    // Create a local field member of type SQLiteDatabase called mDb
    private SQLiteDatabase mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Create a SlimDbHelper instance, pass "this" to the constructor as context
        SlimDBHelper slimDBHelper = new SlimDBHelper(this);

        // Get a writable database reference using getWritableDatabase and store it in mDb
        mDB = slimDBHelper.getWritableDatabase();

        // call insertFakeData from TestUtil and pass the database reference mDb
        TestUtil.insertFakeData(mDB);

        // Run the getAllGuests function and store the result in a Cursor variable
        Cursor cursor = getAllUsers();
        ShowUsers(cursor);
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

    private void ShowUsers(Cursor cursor){
        TextView textView = (TextView) findViewById(R.id.textViewLogo);
        textView.setText("Show Users" + "\n");
        while (cursor.moveToNext()){
            int nameColumnIndex = cursor.getColumnIndex(SlimContract.SlimDB.COLUMN_FIRST_NAME);
            String first_name = cursor.getString(nameColumnIndex);
            textView.append("Frist Name:" + first_name + "\n");
        }
    }
}
