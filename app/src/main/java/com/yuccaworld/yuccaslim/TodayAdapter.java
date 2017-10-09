package com.yuccaworld.yuccaslim;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuccaworld.yuccaslim.data.SlimContract;

import java.text.SimpleDateFormat;
import java.util.Date;

//import android.icu.text.SimpleDateFormat;

/**
 * Created by Yung on 9/3/2017.
 */

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.TodayAdapterViewHolder> {

    private final Context mContext;
    private Cursor mCursor;
    private int mNumberItems = 20;

    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public TodayAdapter(@NonNull Context context) {
        mContext = context;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new ViewHolder that holds the view for each task
     */
    @Override
    public TodayAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.today_list_item, parent, false);

        return new TodayAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodayAdapterViewHolder holder, int position) {
        //ActivityInfo activityInfo = SlimUtils.genFakeActivityInfo();

        int idIndex = mCursor.getColumnIndex(SlimContract.SlimDB._ID);
        int activityTypeIdIndex = mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_ID);
        int activityTimeIndex = mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_ACTIVITY_TIME);
        int activityHintIdIndex = mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_HINT_ID);

        mCursor.moveToPosition(position);
        int id = mCursor.getInt(idIndex);
        int activityTypeId = mCursor.getInt(activityTypeIdIndex);
        int activityHintId = mCursor.getInt(activityHintIdIndex);
        long l = 0;
        String time = "";
        l = mCursor.getLong(activityTimeIndex);
        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        if (l != 0) {
            time = format.format(new Date(l));
        }
        holder.timeView.setText(time);
        holder.hintView.setText("Hint" + String.valueOf(activityHintId));
        holder.ActivityView.setText("Activity" + String.valueOf(position));

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function update the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor updateCursor(Cursor c) {
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        if (c != null) {
            this.notifyDataSetChanged();
            ;
        }
        return temp;
    }

    class TodayAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView hintView;
        final TextView timeView;
        final TextView ActivityView;
        final ImageView taskIcon;
        final ImageView editIcon;

        public TodayAdapterViewHolder(View itemView) {
            super(itemView);
            hintView = (TextView) itemView.findViewById(R.id.textViewHint);
            timeView = (TextView) itemView.findViewById(R.id.textViewTime);
            ActivityView = (TextView) itemView.findViewById(R.id.textViewActivity);
            taskIcon = (ImageView) itemView.findViewById(R.id.imageViewActivityType);
            editIcon = (ImageView) itemView.findViewById(R.id.imageViewEdit);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
