package com.yuccaworld.yuccaslim;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.yuccaworld.yuccaslim.model.Daily;
import com.yuccaworld.yuccaslim.model.FoodFavor;
import com.yuccaworld.yuccaslim.utilities.AppExecutors;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TodayActivity extends AppCompatActivity implements TodayAdapter.TodayAdapterOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TODAY_ACTIVITY_LOADER_ID = 8;
    public static final String EXTRA_ACTIVITY_ID = "extraActivityId";
    public static final String EXTRA_ROW_ID = "ExtraRowID";
    private static int mHoursToDisplay = 30;
    private TodayAdapter mTodayAdapter;
    private RecyclerView mRecyclerView;
    private static Cursor mActivityData = null;
    private DatabaseReference mFirebaseDB;
    private RangeSeekBar mSeekbarToday;
    private LiveData<List<Activity>> mActivityList;
    private LiveData<List<Activity>> mActivityListWeekHistory;
    private LiveData<List<Activity>> mActivityListMonthHistory;
    private AppDatabase mDb;
    private TodayViewModel mTodayViewModel;

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
                        Activity activity = activityList.get(position);
                        mDb.activityDao().deleteActivity(activity);
                        //mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(activityList.get(position).getActivityID()).setValue(null);
                        mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(activityList.get(position).getActivityID()).removeValue();

                        // Update Food Favor count
                        if (activity.getActivityTypeID() == 2) {
                            long l = mDb.FoodFavorDao().lessFoodFavorCountById(activity.getFoodID());
                            if (l>0) {
                                int foodID = activity.getFoodID();
                                FoodFavor foodFavor = mDb.FoodFavorDao().loadFoodFavorById(foodID);
                                String s = String.valueOf(foodID);
                                foodFavor.setUpdateTime(new Date());
                                mFirebaseDB.child("FoodFavor").child(SlimUtils.gUid).child(s).setValue(foodFavor);
                            }
                        }
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        // Get data from firebase Activity Node
        ChildEventListener childEventListenerActivity = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Update the Sqlite and adapter
                final Activity activity = dataSnapshot.getValue(Activity.class);
                String activityID = activity.getActivityID();
                String hintText = activity.getHint();
                int hintID = activity.getHintID();
                int ind1 = activity.getInd1();
                if (activityID != null) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            int i = mDb.activityDao().updateActivity(activity);
                            if (i==0) {long l = mDb.activityDao().insertActivity(activity);}
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    Map<String, String> activityData = (Map)ds.getValue();
//                }
                final Activity activity = dataSnapshot.getValue(Activity.class);
                // Update the Sqlite and adapter
                String activityID = activity.getActivityID();
                String hintText = activity.getHint();
                int hintID = activity.getHintID();
                int ind1 = activity.getInd1();
                if (activityID != null) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.activityDao().updateActivity(activity);
                        }
                    });
                }
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
                // Update Daily data to local DB
                final Daily daily = dataSnapshot.getValue(Daily.class);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                        String currentDate = sdf.format(new Date());
                        int i;
//                        i = mDb.dailyDao().deleteDailyByDate(currentDate);
                        i = mDb.dailyDao().deleteDailyByDate(daily.getDate());
                        Log.v(TAG, "Daily Data deleted:" + i);
                        long l = mDb.dailyDao().insertDaily(daily);
                        Log.v(TAG, "Daily Data Inserted:" + daily.getSlimScore() + " inserted: " + l);
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Update Daily data to local DB
                final Daily daily = dataSnapshot.getValue(Daily.class);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                        String currentDate = sdf.format(new Date());
                        int i;
//                        i = mDb.dailyDao().deleteDailyByDate(currentDate);
                        i = mDb.dailyDao().deleteDailyByDate(daily.getDate());
                        Log.v(TAG, "Daily Data deleted:" + i);
                        long l = mDb.dailyDao().insertDaily(daily);
                        Log.v(TAG, "Daily Data Inserted:" + daily.getSlimScore() + " inserted: " + l);
                    }
                });
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
        mFirebaseDB.child("Daily").child(SlimUtils.gUid).addChildEventListener(childEventListenerDaily);

        setupViewModel();
    }

    private void setupViewModel() {
        // Setup Today Activity View model
        mTodayViewModel = ViewModelProviders.of(this).get(TodayViewModel.class);
        mActivityList = mTodayViewModel.getTodayActivityList();
        mActivityList.observe(this, new Observer<List<Activity>>() {
            @Override
            public void onChanged(@Nullable List<Activity> activityList) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel From TodayActivity oncreate");
                mTodayAdapter.setActivityList(activityList);
//                setSeekBarValue();
            }
        });

        // Setup Daily View model
        mTodayViewModel.getTodayDaily().observe(this, new Observer<Daily>() {
            @Override
            public void onChanged(@Nullable Daily daily) {
                if (daily != null) {
                    mTodayViewModel.daily = daily;
                    setSeekBarValue();
                }
            }
        });
    }

    private void showActivityWeekHistory(int hoursFromNow){
        if (mActivityListWeekHistory == null) {
            mActivityListWeekHistory = mTodayViewModel.getActivityListHistory(hoursFromNow);
            mActivityListWeekHistory.observe(this, new Observer<List<Activity>>() {
                @Override
                public void onChanged(@Nullable List<Activity> activityList) {
                    mTodayAdapter.setActivityList(activityList);
                }
            });
        } else {mTodayAdapter.setActivityList(mActivityListWeekHistory.getValue());}
    }

    private void showActivityMonthHistory(int hoursFromNow){
        if (mActivityListMonthHistory == null) {
            mActivityListMonthHistory = mTodayViewModel.getActivityListHistory(hoursFromNow);
            mActivityListMonthHistory.observe(this, new Observer<List<Activity>>() {
                @Override
                public void onChanged(@Nullable List<Activity> activityList) {
                    mTodayAdapter.setActivityList(activityList);
                }
            });
        } else {mTodayAdapter.setActivityList(mActivityListMonthHistory.getValue());}
    }

//    private void setSeekBarValue(int slimScore, int targetFat, int targetHeavy) {
    private void setSeekBarValue() {
        int slimScore, targetFat, targetHeavy;
        slimScore = mTodayViewModel.daily.getSlimScore();
        targetFat = mTodayViewModel.daily.getTargetFat();
        targetHeavy = mTodayViewModel.daily.getTargetHeavy();
        if (slimScore < 0  ) {slimScore = 0; }
        mSeekbarToday.setValue(slimScore);
        if (slimScore <= targetFat) {
            mSeekbarToday.getLeftSeekBar().setIndicatorBackgroundColor(getResources().getColor(R.color.green));
            mSeekbarToday.setProgressColor(getResources().getColor(R.color.green));
        }
        else if (slimScore <= targetHeavy) {
            mSeekbarToday.getLeftSeekBar().setIndicatorBackgroundColor(getResources().getColor(R.color.yellow));
            mSeekbarToday.setProgressColor(getResources().getColor(R.color.yellow));
        } else {
            mSeekbarToday.getLeftSeekBar().setIndicatorBackgroundColor(getResources().getColor(R.color.colorAccent));
            mSeekbarToday.setProgressColor(getResources().getColor(R.color.colorAccent));
        }
        //mSeekbarToday.invalidate();
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
                mHoursToDisplay = 30;
                mTodayAdapter.setActivityList(mActivityList.getValue());
                break;
            case R.id.last_week :
                mHoursToDisplay = 170;
                showActivityWeekHistory(mHoursToDisplay);
                break;
            case R.id.last_month :
                mHoursToDisplay = 5208;
                showActivityMonthHistory(mHoursToDisplay);
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
    protected void onResume() {
        super.onResume();
        setSeekBarValue();
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
                foodIntent.putExtra(TodayActivity.EXTRA_ROW_ID, rowID);
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
            case TodayAdapter.CHANGE_FOOD_INPUT_TIME:
                Intent foodTimeIntent = new Intent(TodayActivity.this, FoodTimeActivity.class);
                activityMode = "EDIT";
                foodTimeIntent.putExtra(Intent.EXTRA_TEXT, activityMode);
                foodTimeIntent.putExtra(TodayActivity.EXTRA_ACTIVITY_ID, activityID);
                foodTimeIntent.putExtra(TodayActivity.EXTRA_ROW_ID, rowID);
                startActivity(foodTimeIntent);
                break;
            default:
                Log.w(TAG, "No matching type");
        }

    }

}
