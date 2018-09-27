package com.yuccaworld.yuccaslim;

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

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.yuccaworld.yuccaslim.data.SlimContract;

import java.util.ArrayList;
import java.util.List;

public class FoodSearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MainActivity.class.getSimpleName();
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
        mRecyclerView = findViewById(R.id.search_suggestion);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMaterialSearchBar = findViewById(R.id.search_bar);
        
        //Setup search bar
        mMaterialSearchBar.setHint("Search");
        mMaterialSearchBar.setCardViewElevation(10);
        mAdapter = new FoodSearchAdapter(this);
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

    private void startSearch(String s) {
        mSearchInput = s;
        getSupportLoaderManager().getLoader(FOOD_SEARCH_ACTIVITY_LOADER_ID).forceLoad();
        //getSupportLoaderManager().restartLoader(FOOD_SEARCH_ACTIVITY_LOADER_ID, null,this);
        mAdapter = new FoodSearchAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
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
