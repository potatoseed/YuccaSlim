package com.yuccaworld.yuccaslim;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yuccaworld.yuccaslim.data.SlimContract;

public class TodayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, TodayAdapter.TodayAdapterOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TODAY_ACTIVITY_LOADER_ID = 8;
    private TodayAdapter mTodayAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddWeight);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addWeightIntent = new Intent(TodayActivity.this, WeightActivity.class);
                // set weight activity mode to "ADD"
                String weightActivityMode = "ADD";
                addWeightIntent.putExtra(Intent.EXTRA_TEXT, weightActivityMode);
                startActivity(addWeightIntent);
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
                getSupportLoaderManager().restartLoader(TODAY_ACTIVITY_LOADER_ID, null, TodayActivity.this);
            }
        }).attachToRecyclerView(mRecyclerView);
        /*
        Ensure a loader is initialized and active. If the loader doesn't already exist, one is
        created, otherwise the last created loader is re-used.
        */
        getSupportLoaderManager().initLoader(TODAY_ACTIVITY_LOADER_ID, null, this);
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
        Cursor mActivityData = null;

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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(int rowID) {
        Toast.makeText(this, "Clicked" + rowID, Toast.LENGTH_SHORT).show();
        Intent weightIntent = new Intent(TodayActivity.this, WeightActivity.class);
        // set weight activity mode to "EDIT"
        String weightActivityMode = "EDIT";
        weightIntent.putExtra(Intent.EXTRA_TEXT, weightActivityMode);
        Uri uriForActivityClicked = SlimContract.SlimDB.buildWeightEdit(rowID);
        weightIntent.setData(uriForActivityClicked);
        startActivity(weightIntent);
    }
}
