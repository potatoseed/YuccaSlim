package com.yuccaworld.yuccaslim;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.MotionEvent;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaygoo.widget.RangeSeekBar;
import com.yuccaworld.yuccaslim.data.AppDatabase;
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.model.ActivityInfo;
import com.yuccaworld.yuccaslim.model.Daily;
import com.yuccaworld.yuccaslim.model.DailyOld;
import com.yuccaworld.yuccaslim.utilities.AppExecutors;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TodayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, TodayAdapter.TodayAdapterOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TODAY_ACTIVITY_LOADER_ID = 8;
    public static final String EXTRA_ACTIVITY_ID = "extraActivityId";
    private static int mHoursToDisplay = 36;
    private TodayAdapter mTodayAdapter;
    private RecyclerView mRecyclerView;
    private static Cursor mActivityData = null;
    private DatabaseReference mFirebaseDB;
    private RangeSeekBar mSeekbarToday;
    private int mSlimScore = 0;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        mDb = AppDatabase.getInstance(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_today);
        setSupportActionBar(toolbar);
        SlimUtils.gUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SlimUtils.gUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mFirebaseDB = FirebaseDatabase.getInstance().getReference();
        mSeekbarToday = findViewById(R.id.seekBarToday);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddWeight);

        //Seekbar setup
        mSeekbarToday.setIndicatorTextDecimalFormat("0");
        setSeekBarValue(mSlimScore);

        // Disable the user operation
        mSeekbarToday.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // Recycleview setup
        mRecyclerView = (RecyclerView) findViewById(R.id.RecycleViewToday);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mTodayAdapter = new TodayAdapter(this, this);
        mRecyclerView.setAdapter(mTodayAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent addWeightIntent = new Intent(TodayActivity.this, WeightActivity.class);
//                addWeightIntent.putExtra(Intent.EXTRA_TEXT, weightActivityMode);
                Intent addFoodIntent = new Intent(TodayActivity.this, FoodSearchActivity.class);
                startActivity(addFoodIntent);
                //startActivity(addWeightIntent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        // Handel the swap to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<Activity> activityList = mTodayAdapter.getActivityList();
                        mDb.activityDao().deleteActivity(activityList.get(position));
                        //mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(activityList.get(position).getActivityID()).setValue(null);
                        mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(activityList.get(position).getActivityID()).removeValue();
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        // Get data from firebase Activity Node
        ChildEventListener childEventListenerActivity = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    Map<String, String> activityData = (Map)ds.getValue();
//                }
//                ActivityInfo activityInfo = dataSnapshot.getValue(ActivityInfo.class);
//                // Update the Sqlite and adapter
//                String activityID = activityInfo.getActivityID();
//                String hintText = activityInfo.getHint();
//                int hintID = activityInfo.getHintID();
//                int ind1 = activityInfo.getInd1();
//                int updatedRow = 0;
//
//                if (activityID != null) {
//                    Uri uri = SlimContract.SlimDB.CONTENT_ACTIVITY_URI;
//                    uri = uri.buildUpon().appendPath(activityID).build();
//                    ContentValues contentValues = new ContentValues();
//                    contentValues.put(SlimContract.SlimDB.COLUMN_IND1, ind1);
//                    contentValues.put(SlimContract.SlimDB.COLUMN_HINT_ID, hintID);
//                    contentValues.put(SlimContract.SlimDB.COLUMN_HINT_TEXT, hintText);
//                    updatedRow = getContentResolver().update(uri, contentValues, null, null);
//
//                    // Force load to refresh and see the hint
//                    getSupportLoaderManager().restartLoader(TODAY_ACTIVITY_LOADER_ID, null, TodayActivity.this);
//                    getSupportLoaderManager().getLoader(TODAY_ACTIVITY_LOADER_ID).forceLoad();
//                }

                final Activity activity = dataSnapshot.getValue(Activity.class);
                // Update the Sqlite and adapter
                String activityID = activity.getActivityID();
                String hintText = activity.getHint();
                int hintID = activity.getHintID();
                int ind1 = activity.getInd1();
                if (activityID != null) {
                    activity.setHintID(hintID);
                    activity.setHint(hintText);
                    activity.setInd1(ind1);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.activityDao().updateActivity(activity);
                        }
                    });

                    // Force load to refresh and see the hint
                    getSupportLoaderManager().restartLoader(TODAY_ACTIVITY_LOADER_ID, null, TodayActivity.this);
                    getSupportLoaderManager().getLoader(TODAY_ACTIVITY_LOADER_ID).forceLoad();
                }


//                Log.v(TAG, "DataInSnap:" + dataSnapshot.getValue());
//                Log.v(TAG, "DataInMap:" + activityInfo);
//                Log.v(TAG, "DataInFields : HindID=" + hintID + " ActivityID=" + activityID + " ind1=" + ind1 + " UpdateRow=" + updatedRow);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mFirebaseDB.child("Activity").child(SlimUtils.gUid).addChildEventListener(childEventListenerActivity);

        // Get data from firebase Daily Node
        ChildEventListener childEventListenerDaily = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot == null) {
                    return ;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sdf.format(new Date());
                DailyOld dailyOld = dataSnapshot.getValue(DailyOld.class);
                mSlimScore = dailyOld.getSlimScore();
//                currentDate = dataSnapshot.getKey();
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(SlimContract.SlimDB.COLUMN_DATE, currentDate);
//                contentValues.put(SlimContract.SlimDB.COLUMN_SLIM_SCORE, mSlimScore);
//                contentValues.put(SlimContract.SlimDB.COLUMN_USER_ID, SlimUtils.gUid);
//                Uri uri = SlimContract.SlimDB.CONTENT_ACTIVITY_URI;
//                uri = uri.buildUpon().appendPath(activityID).build();

                final Daily daily = new Daily(currentDate,SlimUtils.gUid,mSlimScore,100,150,new Date());
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.dailyDao().insertDaily(daily);
                    }
                });
                setSeekBarValue(mSlimScore);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mFirebaseDB.child("DailyOld").child(SlimUtils.gUid).addChildEventListener(childEventListenerDaily);

        /*
        Ensure a loader is initialized and active. If the loader doesn't already exist, one is
        created, otherwise the last created loader is re-used.
        */
//        getSupportLoaderManager().initLoader(TODAY_ACTIVITY_LOADER_ID, null, this);
//        getSupportLoaderManager().getLoader(TODAY_ACTIVITY_LOADER_ID).forceLoad();
        setupViewModel();
    }

    private void setupViewModel() {
        TodayViewModel viewModel = ViewModelProviders.of(this).get(TodayViewModel.class);
        viewModel.getActivityList().observe(this, new Observer<List<Activity>>() {
            @Override
            public void onChanged(@Nullable List<Activity> activityList) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel From TodayActivity oncreate");
                mTodayAdapter.setActivityList(activityList);
            }
        });
    }

    private void setSeekBarValue(int i) {
        mSeekbarToday.setValue(i);
        mSeekbarToday.invalidate();
        if (i <= 100) {
            mSeekbarToday.getLeftSeekBar().setIndicatorBackgroundColor(getResources().getColor(R.color.green));
            mSeekbarToday.setProgressColor(getResources().getColor(R.color.green));
        }
        else if (i <= 150) {
            mSeekbarToday.getLeftSeekBar().setIndicatorBackgroundColor(getResources().getColor(R.color.yellow));
            mSeekbarToday.setProgressColor(getResources().getColor(R.color.yellow));
        } else {
            mSeekbarToday.getLeftSeekBar().setIndicatorBackgroundColor(getResources().getColor(R.color.colorAccent));
            mSeekbarToday.setProgressColor(getResources().getColor(R.color.colorAccent));
        }
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
//            case R.id.add_food :
//                Intent addFoodIntent = new Intent(TodayActivity.this, FoodSearchActivity.class);
//                startActivity(addFoodIntent);
//                break;
            case R.id.menu_add_sleep :
                Intent addSleepIntent = new Intent(TodayActivity.this, SleepActivity.class);
                startActivity(addSleepIntent);
                break;
            case R.id.today :
                mHoursToDisplay = 36;
                getSupportLoaderManager().restartLoader(TODAY_ACTIVITY_LOADER_ID, null, TodayActivity.this);
                getSupportLoaderManager().getLoader(TODAY_ACTIVITY_LOADER_ID).forceLoad();
                break;
            case R.id.last_week :
                mHoursToDisplay = 170;
                getSupportLoaderManager().restartLoader(TODAY_ACTIVITY_LOADER_ID, null, TodayActivity.this);
                getSupportLoaderManager().getLoader(TODAY_ACTIVITY_LOADER_ID).forceLoad();
                break;
            case R.id.last_month :
                mHoursToDisplay = 5208;
                getSupportLoaderManager().restartLoader(TODAY_ACTIVITY_LOADER_ID, null, TodayActivity.this);
                getSupportLoaderManager().getLoader(TODAY_ACTIVITY_LOADER_ID).forceLoad();
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
//            try {
//                Cursor cursor =
//                                getContext().getContentResolver().query(SlimContract.SlimDB.CONTENT_ACTIVITY_URI,
//                                        null,
//                                        "hours_from_now <= " + Integer.toString(mHoursToDisplay),
//                                        null,
//                                        SlimContract.SlimDB.COLUMN_ACTIVITY_TIME);
//                return cursor;
//            } catch (Exception e) {
//                Log.e(TAG, "Failed to asynchronously load data.");
//                e.printStackTrace();
//                return null;
//            }
            return null;
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

        setSeekBarValue(mSlimScore);
        getSupportLoaderManager().restartLoader(TODAY_ACTIVITY_LOADER_ID, null, this);
        getSupportLoaderManager().getLoader(TODAY_ACTIVITY_LOADER_ID).forceLoad();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(int rowID, int typeID, String activityID) {
        //Toast.makeText(this, "Clicked" + rowID, Toast.LENGTH_SHORT).show();
        String activityMode;
        Uri uriForActivityClicked;
        //String activityID = mActivityData.getString(mActivityData.getColumnIndex(SlimContract.SlimDB.COLUMN_ACTIVITY_ID));
        switch(typeID){
            case 1:
                Intent weightIntent = new Intent(TodayActivity.this, WeightActivity.class);
                // set weight activity mode to "EDIT"
                activityMode = "EDIT";
                weightIntent.putExtra(Intent.EXTRA_TEXT, activityMode);
                weightIntent.putExtra(TodayActivity.EXTRA_ACTIVITY_ID, activityID);
                weightIntent.putExtra(WeightActivity.EXTRA_ROW_ID, rowID);
                uriForActivityClicked = SlimContract.SlimDB.buildWeightEdit(rowID);
                weightIntent.setData(uriForActivityClicked);
                startActivity(weightIntent);
                break;
            case 2:
                Intent foodIntent = new Intent(TodayActivity.this, FoodSearchActivity.class);
                activityMode = "EDIT";
                foodIntent.putExtra(Intent.EXTRA_TEXT, activityMode);
                foodIntent.putExtra(TodayActivity.EXTRA_ACTIVITY_ID, activityID);
                foodIntent.putExtra(WeightActivity.EXTRA_ROW_ID, rowID);
                uriForActivityClicked = SlimContract.SlimDB.buildFoodEdit(rowID);
                foodIntent.setData(uriForActivityClicked);
                startActivity(foodIntent);
                break;
            case 3:
                Intent sleepIntent = new Intent(TodayActivity.this, SleepActivity.class);
                activityMode = "EDIT";
                sleepIntent.putExtra(Intent.EXTRA_TEXT, activityMode);
                sleepIntent.putExtra(TodayActivity.EXTRA_ACTIVITY_ID, activityID);
                sleepIntent.putExtra(WeightActivity.EXTRA_ROW_ID, rowID);
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
//        getSupportLoaderManager().getLoader(TODAY_ACTIVITY_LOADER_ID).forceLoad();
    }
}
