package com.yuccaworld.yuccaslim;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mobsandgeeks.saripaar.annotation.DecimalMax;
import com.mobsandgeeks.saripaar.annotation.DecimalMin;
import com.mobsandgeeks.saripaar.annotation.Or;
import com.yuccaworld.yuccaslim.data.AppDatabase;
import com.yuccaworld.yuccaslim.data.FoodViewModel;
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.model.ActivityInfo;
import com.yuccaworld.yuccaslim.model.Food;
import com.yuccaworld.yuccaslim.utilities.AppExecutors;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FoodSearchActivity extends AppActivity implements FoodSearchAdapter.FoodItemClickListerner {
    @DecimalMin(value = 1, sequence = 1, messageResId = R.string.min_food_validation)
    @Or
    @DecimalMax(value = 1000, sequence = 2, messageResId = R.string.max_food_validation)
    private EditText mEditTextFoodQty;
    private TextView mTextViewFoodID;
    private static final String TAG = FoodSearchActivity.class.getSimpleName();
    private static Uri mUri;
    private String mMode = "";
    RecyclerView mRecyclerView;
    FoodSearchAdapter mAdapter;
    MaterialSearchBar mMaterialSearchBar;
    private static final int FOOD_SEARCH_ACTIVITY_LOADER_ID = 12;
    List<String> mSuggestList = new ArrayList<>();
    private static String mSearchInput = null;
    private DatabaseReference mFirebaseDB;
    private String mActivityID ="";
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_search);
        mDb = AppDatabase.getInstance(getApplicationContext());
        SlimUtils.gUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SlimUtils.gUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mFirebaseDB = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            mMode = intent.getStringExtra(Intent.EXTRA_TEXT);
            mActivityID = intent.getStringExtra(TodayActivity.EXTRA_ACTIVITY_ID);
        }

        // Setup RecycleView
        mRecyclerView = findViewById(R.id.search_suggestion);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new FoodSearchAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        //Setup search bar
        mMaterialSearchBar = findViewById(R.id.search_bar);
        mMaterialSearchBar.setHint("Search");
        mMaterialSearchBar.setCardViewElevation(10);
        mMaterialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search: mSuggestList){
                    if(search.toLowerCase().contains(mMaterialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                mMaterialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mMaterialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled){
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
        setupViewModel();
    }

    private void setupViewModel() {
        FoodViewModel viewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
        viewModel.getFoodList().observe(this, new Observer<List<Food>>() {
            @Override
            public void onChanged(@Nullable List<Food> foods) {
                mAdapter.setFoodList(foods);
                // init search bar suggestion list
                for (int i=0; i<foods.size(); i++) {
                    mSuggestList.add(foods.get(i).getFoodName());
                }
                mMaterialSearchBar.setLastSuggestions(mSuggestList);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Prevent the runtime error "FAILED BINDER TRANSACTION !!! (parcel size =" caused by big food state dta.
        outState.clear();
    }

    private void startSearch(String s) {
        final String searchInput = s;
        getSupportLoaderManager().getLoader(FOOD_SEARCH_ACTIVITY_LOADER_ID).forceLoad();
        //getSupportLoaderManager().restartLoader(FOOD_SEARCH_ACTIVITY_LOADER_ID, null,this);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Food> foods = mDb.FoodDao().loadFoodLikeName(searchInput);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setFoodList(foods);
                        for (int i=0; i<foods.size(); i++) {
                            mSuggestList.add(foods.get(i).getFoodName());
                        }
                        mMaterialSearchBar.setLastSuggestions(mSuggestList);
                    }
                });
            }
        });
        mAdapter = new FoodSearchAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int clickedPosition, int foodId) {
        View viewItem = mRecyclerView.getLayoutManager().findViewByPosition(clickedPosition);
        mEditTextFoodQty = viewItem.findViewById(R.id.editTextFoodQty);
        mTextViewFoodID = viewItem.findViewById(R.id.textViewFoodID);
        TextView textViewfoodName = viewItem.findViewById(R.id.textViewFoodName);

        validator.validate();
        if (validated) {

//            UUID uuid = UUID.randomUUID();
//            byte[] activityID = SlimUtils.toByte(uuid);
//            contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_ID, activityID);

            // Get Activity Time
            Calendar inpuTime = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH：mm：ss");
            String currentDateandTime = sdf.format(new Date());
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            final String currentDate = sdf.format(new Date());

            // Inputted food Qty in g
            String inputQty = mEditTextFoodQty.getText().toString();
            final String foodName = textViewfoodName.getText().toString();
            if (inputQty.length() == 0) {
                return;
            }
            float t = -1;
            try {
                t = Float.parseFloat(inputQty);
            } catch (ParseException e) {
                e.printStackTrace();
                Snackbar.make(mRecyclerView, "Invalid Number Input", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            final int foodID = foodId;
            final long activityTime = inpuTime.getTimeInMillis();
            final float foodQty = t;
            final String uid = UUID.randomUUID().toString();
            // Insert in Sqlite DB and Upload to firebase realtime DB
            if (mActivityID == "") {mActivityID = uid.toString();}

            // Update DB and Firebase
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if ("EDIT".equals(mMode)){
                        // Update
                        final Activity activity = new Activity(uid,SlimUtils.gUid,SlimUtils.gUserEmail,2, getResources().getString(R.string.activity_type_2), activityTime,foodID,foodName,0,foodQty,"",0,"",0,0,currentDate,new Date());
                        int i = mDb.activityDao().updateActivity(activity);
                        if (i > 0) {
                            mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(mActivityID).setValue(activity);
                        }
                    } else {
                        // Insert
                        String uid = UUID.randomUUID().toString();
                        final Activity activity = new Activity(uid,SlimUtils.gUid,SlimUtils.gUserEmail,2, getResources().getString(R.string.activity_type_2), activityTime,foodID,foodName,0,foodQty,"",0,"",0,0,currentDate,new Date());
                        long l = mDb.activityDao().insertActivity(activity);
                        if (l > 0) {
                            mFirebaseDB.child("Activity").child(SlimUtils.gUid).child(uid).setValue(activity);
                        }
                    }
                }
            });
        }
        finish();
    }
}
