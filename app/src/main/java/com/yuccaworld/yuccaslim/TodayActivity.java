package com.yuccaworld.yuccaslim;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

public class TodayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, TodayAdapter.TodayAdapterOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TODAY_ACTIVITY_LOADER_ID = 8;
    private static int mHoursToDisplay = 36;
    private TodayAdapter mTodayAdapter;
    private RecyclerView mRecyclerView;
    private static Cursor mActivityData = null;
    private DatabaseReference mFirebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_today);
        setSupportActionBar(toolbar);
        SlimUtils.gUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SlimUtils.gUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mFirebaseDB = FirebaseDatabase.getInstance().getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddWeight);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent addWeightIntent = new Intent(TodayActivity.this, WeightActivity.class);
//                // set weight activity mode to "ADD"
//                String weightActivityMode = "ADD";
//                addWeightIntent.putExtra(Intent.EXTRA_TEXT, weightActivityMode);
                Intent addFoodIntent = new Intent(TodayActivity.this, FoodSearchActivity.class);
                startActivity(addFoodIntent);
                //startActivity(addWeightIntent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.RecycleViewToday);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mTodayAdapter = new TodayAdapter(this, this);
        mRecyclerView.setAdapter(mTodayAdapter);

        // Handel the swap to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (int) viewHolder.itemView.getTag();
                String stringID = Integer.toString(id);
                Uri uri = SlimContract.SlimDB.CONTENT_ACTIVITY_URI;
                uri = uri.buildUpon().appendPath(stringID).build();
                getContentResolver().delete(uri,null,null);
                int position = viewHolder.getAdapterPosition();
                mActivityData.moveToPosition(position);
                String activityID = mActivityData.getString(mActivityData.getColumnIndex(SlimContract.SlimDB.COLUMN_ACTIVITY_ID));
                mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(activityID).setValue(null);
                getSupportLoaderManager().restartLoader(TODAY_ACTIVITY_LOADER_ID, null, TodayActivity.this);
                getSupportLoaderManager().getLoader(TODAY_ACTIVITY_LOADER_ID).forceLoad();
            }
        }).attachToRecyclerView(mRecyclerView);

        /*
        Ensure a loader is initialized and active. If the loader doesn't already exist, one is
        created, otherwise the last created loader is re-used.
        */
        getSupportLoaderManager().initLoader(TODAY_ACTIVITY_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_today_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();
        switch (menuItemSelected) {
            case R.id.add_weight :
                Intent addWeightIntent = new Intent(TodayActivity.this, WeightActivity.class);
                // set weight activity mode to "ADD"
                String weightActivityMode = "ADD";
                addWeightIntent.putExtra(Intent.EXTRA_TEXT, weightActivityMode);
                startActivity(addWeightIntent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                break;
            case R.id.add_food :
                Intent addFoodIntent = new Intent(TodayActivity.this, FoodSearchActivity.class);
                startActivity(addFoodIntent);
                break;
            case R.id.menu_add_sleep :
                Intent addSleepIntent = new Intent(TodayActivity.this, SleepActivity.class);
                startActivity(addSleepIntent);
                break;
            case R.id.last_week :
                mHoursToDisplay = 170;
                getSupportLoaderManager().restartLoader(TODAY_ACTIVITY_LOADER_ID, null, TodayActivity.this);
                break;
            case R.id.last_month :
                mHoursToDisplay = 5208;
                getSupportLoaderManager().restartLoader(TODAY_ACTIVITY_LOADER_ID, null, TodayActivity.this);
                break;
            case R.id.logout:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Intent mainIntent = new Intent(TodayActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                }
                                else {
                                    Log.w(TAG, "signOut:failure", task.getException());
                                    Snackbar.make(mRecyclerView, "Sign out Failure", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case TODAY_ACTIVITY_LOADER_ID:
                return new TodayActivityAsyncTaskLoader(this);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
/*
        return new AsyncTaskLoader<Cursor>(this) {
            // Initialize a Cursor, this will hold all the Activity data
            Cursor mActivityData = null;

            @Override
            protected void onStartLoading() {
                if (mActivityData != null) {
                    deliverResult(mActivityData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    Cursor cursor =
                            getContentResolver().query(SlimContract.SlimDB.CONTENT_ACTIVITY_URI,
                                    null,
                                    null,
                                    null,
                                    SlimContract.SlimDB.COLUMN_ACTIVITY_TIME);
                    return cursor;
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mActivityData = data;
                super.deliverResult(data);
            }
        };
*/
    }
    public static class TodayActivityAsyncTaskLoader extends AsyncTaskLoader<Cursor>{
        // Initialize a Cursor, this will hold all the Activity data
//        Cursor mActivityData = null;

        public TodayActivityAsyncTaskLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (mActivityData != null) {
                deliverResult(mActivityData);
            } else {
                forceLoad();
            }
        }

        @Override
        public Cursor loadInBackground() {
            try {
                Cursor cursor =
                                getContext().getContentResolver().query(SlimContract.SlimDB.CONTENT_ACTIVITY_URI,
                                        null,
                                        "hours_from_now <= " + Integer.toString(mHoursToDisplay),
                                        null,
                                        SlimContract.SlimDB.COLUMN_ACTIVITY_TIME);
                return cursor;
            } catch (Exception e) {
                Log.e(TAG, "Failed to asynchronously load data.");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(Cursor data) {
            mActivityData = data;
            super.deliverResult(data);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mTodayAdapter.updateCursor(data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all activities
        getSupportLoaderManager().restartLoader(TODAY_ACTIVITY_LOADER_ID, null, this);
        getSupportLoaderManager().getLoader(TODAY_ACTIVITY_LOADER_ID).forceLoad();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(int rowID, int typeID) {
        //Toast.makeText(this, "Clicked" + rowID, Toast.LENGTH_SHORT).show();
        String activityMode;
        Uri uriForActivityClicked;
        String activityID = mActivityData.getString(mActivityData.getColumnIndex(SlimContract.SlimDB.COLUMN_ACTIVITY_ID));
        switch(typeID){
            case 1:
                Intent weightIntent = new Intent(TodayActivity.this, WeightActivity.class);
                // set weight activity mode to "EDIT"
                activityMode = "EDIT";
                weightIntent.putExtra(Intent.EXTRA_TEXT, activityMode);
                weightIntent.putExtra(Intent.EXTRA_UID, activityID);
                uriForActivityClicked = SlimContract.SlimDB.buildWeightEdit(rowID);
                weightIntent.setData(uriForActivityClicked);
                startActivity(weightIntent);
                break;
            case 2:
                Intent foodIntent = new Intent(TodayActivity.this, FoodSearchActivity.class);
                activityMode = "EDIT";
                foodIntent.putExtra(Intent.EXTRA_TEXT, activityMode);
                foodIntent.putExtra(Intent.EXTRA_UID, activityID);
                uriForActivityClicked = SlimContract.SlimDB.buildFoodEdit(rowID);
                foodIntent.setData(uriForActivityClicked);
                startActivity(foodIntent);
                break;
            case 3:
                Intent sleepIntent = new Intent(TodayActivity.this, SleepActivity.class);
                activityMode = "EDIT";
                sleepIntent.putExtra(Intent.EXTRA_TEXT, activityMode);
                sleepIntent.putExtra(Intent.EXTRA_UID, activityID);
                uriForActivityClicked = SlimContract.SlimDB.buildFoodEdit(rowID);
                sleepIntent.setData(uriForActivityClicked);
                startActivity(sleepIntent);
                break;
            default:
                Log.w(TAG, "No matching type");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().getLoader(TODAY_ACTIVITY_LOADER_ID).forceLoad();
    }
}
