package com.yuccaworld.yuccaslim;

import android.content.Context;
import android.database.Cursor;
//import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yuccaworld.yuccaslim.data.SlimContract;
//import com.yuccaworld.yuccaslim.databinding.TodayListItemBinding;
import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.yuccaworld.yuccaslim.data.SlimContract.SlimDB.TEXT_VALUE_SLEEP;

//import android.icu.text.SimpleDateFormat;

/**
 * Created by Yung on 9/3/2017.
 */

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.TodayAdapterViewHolder> {
    private final Context mContext;
    public Cursor mCursor;
    private int mNumberItems = 20;
    private List<Activity> mActivityList;

    final private TodayAdapterOnClickHandler mClickHandler;
    /**
     * The interface that receives onClick messages.
     */
    public interface TodayAdapterOnClickHandler {
        void onClick(int rowID, int typeID, String activityID);
    }

    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param ClickHandler
     */
    public TodayAdapter(@NonNull Context context, TodayAdapterOnClickHandler ClickHandler) {
        mContext = context;
        this.mClickHandler = ClickHandler;
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
    public void onBindViewHolder(TodayAdapterViewHolder holder, final int position) {
        // Get data from list
        Activity activity = mActivityList.get(position);
        String hintText = activity.getHint();
        int ind1 = activity.getInd1();
        int id = activity.getId();
        int activityTypeId = activity.getActivityTypeID();
        int activityHintId = activity.getHintID();
        String activityTypeDesc =  activity.getActivityTypeDesc();
        float valueDecimal = activity.getValueDecimal();
        long l = activity.getActivityTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM");
        String time = "", day = "";
        if (l != 0) {
            time = timeFormat.format(new Date(l));
            long nowInMS= System.currentTimeMillis();
            if (DateUtils.isToday(l)) {
                day = mContext.getResources().getString(R.string.text_today);
            } else if (SlimUtils.isYesterday(l)){
                day = mContext.getResources().getString(R.string.text_yesterday);
            } else {
                day = dateFormat.format(new Date(l));
            }
        }
        String imagePath = "@drawable/" + mContext.getResources().getString(R.string.activity_type_image) + String.valueOf(activityTypeId);
        int imageID = mContext.getResources().getIdentifier(imagePath, null, mContext.getPackageName());
        Drawable image = mContext.getResources().getDrawable(imageID, mContext.getApplicationContext().getTheme());

                // Set the view holder value
        holder.itemView.setTag(id);
        holder.activityTypeIcon.setImageDrawable(image);
        holder.hintView.setText(hintText);
        holder.ActivityView.setTextColor(mContext.getResources().getColor(R.color.colorSubText));
        switch(activityTypeId){
            //TODO change to value from DB
            case 1:  //Weight
                activityTypeDesc = activity.getActivityTypeDesc();
                holder.ActivityView.setText(activityTypeDesc.toUpperCase() + " : " + String.valueOf(valueDecimal) + " kg");
                holder.ActivityView.setTextColor(mContext.getResources().getColor(R.color.browser_actions_text_color));
                holder.ValueView.setText("");
                break;
            case 2: // Food
//                holder.ActivityView.setText(mCursor.getString(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_FOOD_NAME)));
                holder.ValueView.setText(String.valueOf(Math.round(valueDecimal)) + " g");
                holder.ActivityView.setText(activity.getFoodName());
                switch (ind1) {
                    case 1:
                        holder.ActivityView.setTextColor(mContext.getResources().getColor(R.color.green));
                        //holder.hintView.setTextColor(mContext.getResources().getColor(R.color.DarkGreen));
                        holder.hintView.setTextColor(mContext.getResources().getColor(R.color.colorSkyBlue));
                        break;
                    case 2:
                        holder.ActivityView.setTextColor(mContext.getResources().getColor(R.color.yellow));
                        holder.hintView.setTextColor(mContext.getResources().getColor(R.color.Orange));
                        holder.hintView.setTextColor(mContext.getResources().getColor(R.color.colorSkyBlue));
                        break;
                    case 3:
                        holder.ActivityView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                        holder.hintView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                        holder.hintView.setTextColor(mContext.getResources().getColor(R.color.colorSkyBlue));
                        break;
                    default:
                }
                break;
            case 3: // Sleep
                holder.ActivityView.setText(activity.getValueText());
                if (activity.getValueText().equals(TEXT_VALUE_SLEEP)) {
                    holder.activityTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sleep_black_24dp));
                } else {
                    holder.activityTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_wake_up_24dp));
                }
                holder.ValueView.setText("");
                break;
            default:
        }

        holder.timeView.setText(time);
        holder.dayView.setText(day);
    }

    @Override
    public int getItemCount() {
        if (mActivityList == null) {
            return 0;
        }
        return mActivityList.size();
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
        }
        return temp;
    }

    public List<Activity> getmActivityList() {return mActivityList;}
    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setActivityList(List<Activity> activityEntry) {
        mActivityList = activityEntry;
        notifyDataSetChanged();
    }

    public List<Activity> getActivityList(){
        return mActivityList;
    }

    class TodayAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView hintView;
        final TextView timeView;
        final TextView dayView;
        final TextView ActivityView;
        final TextView ValueView;
        final ImageView activityTypeIcon;
        //final ImageView editIcon;

        public TodayAdapterViewHolder(View itemView) {
            super(itemView);
            //Get references to all new views
            hintView = (TextView) itemView.findViewById(R.id.textViewHint);
            timeView = (TextView) itemView.findViewById(R.id.textViewTime);
            dayView = itemView.findViewById(R.id.textViewActivityDay);
            ActivityView = (TextView) itemView.findViewById(R.id.textViewActivity);
            ValueView = (TextView) itemView.findViewById(R.id.textViewValue);
            activityTypeIcon = (ImageView) itemView.findViewById(R.id.imageViewActivityType);
            //editIcon = (ImageView) itemView.findViewById(R.id.imageViewEdit);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            int adapterPosition = getAdapterPosition();
//            mCursor.moveToPosition(adapterPosition);
//            //String activityUUID = mCursor.getString(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_ACTIVITY_ID));
//            int rowID = mCursor.getInt(mCursor.getColumnIndex(SlimContract.SlimDB._ID));
//            int typeID = mCursor.getInt(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_ID));

            Activity activity = mActivityList.get(getAdapterPosition());
            int rowID = activity.getId();
            int typeID = activity.getActivityTypeID();
            String activityID = activity.getActivityID();
            mClickHandler.onClick(rowID, typeID, activityID);
        }
    }
}
