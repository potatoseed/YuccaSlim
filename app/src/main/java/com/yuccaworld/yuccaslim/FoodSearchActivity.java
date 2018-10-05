package com.yuccaworld.yuccaslim;

import android.content.ContentValues;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.DecimalMax;
import com.mobsandgeeks.saripaar.annotation.DecimalMin;
import com.mobsandgeeks.saripaar.annotation.Or;
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class FoodSearchActivity extends AppActivity implements LoaderManager.LoaderCallbacks<Cursor>, FoodSearchAdapter.FoodItemClickListerner {
    @DecimalMin(value = 1, sequence = 1, messageResId = R.string.min_food_validation)
    @Or
    @DecimalMax(value = 1000, sequence = 2, messageResId = R.string.max_food_validation)
    private EditText mEditTextFoodQty;

    private TextView mTextViewFoodID;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static Uri mUri;
    private String mMode = "";
    RecyclerView mRecyclerView;
    FoodSearchAdapter mAdapter;
    MaterialSearchBar mMaterialSearchBar;
    private static final int FOOD_SEARCH_ACTIVITY_LOADER_ID = 12;
    List<String> suggestList = new ArrayList<>();
    private static String mSearchInput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_search);
        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            mMode = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        if ("EDIT".equals(mMode)) {
            mUri = getIntent().getData();
            LoaderManager loaderManager = getSupportLoaderManager();
            if (loaderManager == null){
                loaderManager.initLoader(FOOD_SEARCH_ACTIVITY_LOADER_ID, null, this);
            } else {
                loaderManager.restartLoader(FOOD_SEARCH_ACTIVITY_LOADER_ID, null, this);
            }
        }

        mRecyclerView = findViewById(R.id.search_suggestion);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMaterialSearchBar = findViewById(R.id.search_bar);

        //Setup search bar
        mMaterialSearchBar.setHint("Search");
        mMaterialSearchBar.setCardViewElevation(10);
        mAdapter = new FoodSearchAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(FOOD_SEARCH_ACTIVITY_LOADER_ID, null,  this);
        //loadSuggestList();
        mMaterialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search:suggestList){
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Prevent the runtime error "FAILED BINDER TRANSACTION !!! (parcel size =" caused by big food state dta.
        outState.clear();
    }

    private void startSearch(String s) {
        mSearchInput = s;
        getSupportLoaderManager().getLoader(FOOD_SEARCH_ACTIVITY_LOADER_ID).forceLoad();
        //getSupportLoaderManager().restartLoader(FOOD_SEARCH_ACTIVITY_LOADER_ID, null,this);
        mAdapter = new FoodSearchAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int clickedPosition) {

        View viewItem = mRecyclerView.getLayoutManager().findViewByPosition(clickedPosition);
        mEditTextFoodQty = viewItem.findViewById(R.id.editTextFoodQty);
        mTextViewFoodID = viewItem.findViewById(R.id.textViewFoodID);

        validator.validate();
        if (validated) {
            ContentValues contentValues = new ContentValues();
            UUID uuid = UUID.randomUUID();
            byte[] bytes = SlimUtils.toByte(uuid);
            contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_ID, bytes);

            // Activity type id=2 for food taken
            contentValues.put(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_ID, 2);

            // Get Activity Time
            Calendar inpuTime = Calendar.getInstance();
            contentValues.put(SlimContract.SlimDB.COLUMN_ACTIVITY_TIME, inpuTime.getTimeInMillis());

            // TODO Fill in Hint ID by other logic later
            contentValues.put(SlimContract.SlimDB.COLUMN_HINT_ID, 1);

            // Inputted food Qty in g
            String input = mEditTextFoodQty.getText().toString();
            String input2 = mTextViewFoodID.getText().toString();
            float foodQty = -1;
            int foodID = -1;
            if (input.length() == 0) {
                return;
            }
            try {
                foodQty = Float.parseFloat(input);

            } catch (ParseException e) {
                e.printStackTrace();
                Snackbar.make(mRecyclerView, "Invalid Number Input", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

            if (input2.length() == 0) {
                return;
            }
            try {
                foodID = Integer.parseInt(input2);

            } catch (ParseException e) {
                e.printStackTrace();
                Snackbar.make(mRecyclerView, "Invalid Number Input", Snackbar.LENGTH_LONG).setAction("Action", null).show();
             }
            contentValues.put(SlimContract.SlimDB.COLUMN_VALUE_DECIMAL, foodQty);
            contentValues.put(SlimContract.SlimDB.COLUMN_FOOD_ID, foodID);

            Uri uri = null;
            int updatedRow = 0;
            if ("EDIT".equals(mMode)) {
                updatedRow = getContentResolver().update(mUri,contentValues,null,null);
            } else {
                uri = getContentResolver().insert(SlimContract.SlimDB.CONTENT_ACTIVITY_URI, contentValues);
                if (uri == null)
                    Toast.makeText(this, "uri is null" + uri, Toast.LENGTH_SHORT).show();
                //else Toast.makeText(this, "Clicked" + clickedPosition + "  Input qty is: " + foodQty + " uri: " + uri, Toast.LENGTH_SHORT).show();

            }
            finish();
        }

    }

    public static class SearchActivityAsyncTaskLoader extends AsyncTaskLoader<Cursor> {
        Cursor mSearchData = null;

        public SearchActivityAsyncTaskLoader(Context context) {
            super(context);
        }

        @Override
        public void deliverResult(Cursor data) {
            mSearchData = data;
            super.deliverResult(data);
        }

        @Override
        public Cursor loadInBackground() {
            try {
                Cursor cursor;
                if (mSearchInput == null)
                    cursor = getContext().getContentResolver().query(SlimContract.SlimDB.CONTENT_FOOD_URI, null, null, null, SlimContract.SlimDB.COLUMN_FOOD_NAME);
                else
                    cursor = getContext().getContentResolver().query(SlimContract.SlimDB.CONTENT_FOOD_URI, null, "food_name like '%" +mSearchInput+"%'", null, SlimContract.SlimDB.COLUMN_FOOD_NAME);
                return cursor;
            } catch (Exception e) {
                Log.e(TAG, "Failed to asynchronously load data.");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (mSearchData != null) {
                deliverResult(mSearchData);
            } else {
                forceLoad();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FOOD_SEARCH_ACTIVITY_LOADER_ID:
                return new SearchActivityAsyncTaskLoader(this);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.updateCursor(data);

        // init search bar suggestion list
        if(data.moveToFirst()){
            do {
                suggestList.add(data.getString(data.getColumnIndex(SlimContract.SlimDB.COLUMN_FOOD_NAME)));
            } while (data.moveToNext());
        }
        mMaterialSearchBar.setLastSuggestions(suggestList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all activities
        getSupportLoaderManager().restartLoader(FOOD_SEARCH_ACTIVITY_LOADER_ID, null,this);
    }


}