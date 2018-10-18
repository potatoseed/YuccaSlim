package com.yuccaworld.yuccaslim;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
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
import com.yuccaworld.yuccaslim.databinding.TodayListItemBinding;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    /*
  * Below, we've defined an interface to handle clicks on items within this Adapter. In the
  * constructor of our ForecastAdapter, we receive an instance of a class that has implemented
  * said interface. We store that instance in this variable to call the onClick method whenever
  * an item is clicked in the list.
  */
    final private TodayAdapterOnClickHandler mClickHandler;


    /**
     * The interface that receives onClick messages.
     */
    public interface TodayAdapterOnClickHandler {
        void onClick(int rowID, int typeID);
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
        //ActivityInfo activityInfo = SlimUtils.genFakeActivityInfo();

        int idIndex = mCursor.getColumnIndex(SlimContract.SlimDB._ID);
        int activityTypeIdIndex = mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_ID);
        int activityTypeDescIndex = mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_DESC);
        int activityTypeImagePath = mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_ICON_IMAGE_PATH);
        int activityTimeIndex = mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_ACTIVITY_TIME);
        int activityValueDecimalIndex = mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_VALUE_DECIMAL);
        int activityHintIdIndex = mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_HINT_ID);
        int foodIDIndex = mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_FOOD_ID);

        // Get data from cursor
        mCursor.moveToPosition(position);
        String hintText = mCursor.getString(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_HINT_TEXT));
        int ind1 = mCursor.getInt(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_IND1));
        int id = mCursor.getInt(idIndex);
        holder.itemView.setTag(id);
        int activityTypeId = mCursor.getInt(activityTypeIdIndex);
        int activityHintId = mCursor.getInt(activityHintIdIndex);
        long l = 0;
        String time = "", day = "";
        String activityTypeDesc="";
        float valueDecimal = 0;
        // Get Activity time
        l = mCursor.getLong(activityTimeIndex);
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM");
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
//            if (TimeUnit.MILLISECONDS.toHours(Math.abs(l - nowInMS)) < 24) {
//                day = mContext.getResources().getString(R.string.text_today);
//            } else {
//                day = dateFormat.format(new Date(l));
//            }

        // Set activity image
        String imagePath = mCursor.getString(activityTypeImagePath);
        String packageName = mContext.getPackageName();
        int imageID = mContext.getResources().getIdentifier(imagePath, null, packageName);
        Drawable image = mContext.getResources().getDrawable(imageID, mContext.getApplicationContext().getTheme());

        //Get Activity Type Desc
        activityTypeDesc = mCursor.getString(activityTypeDescIndex);

        //Get Activity Value
        valueDecimal = mCursor.getFloat(activityValueDecimalIndex);

        holder.activityTypeIcon.setImageDrawable(image);
        switch(activityTypeId){
            //TODO change to value from DB
            case 1:  //Weight
                activityTypeDesc = mCursor.getString(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_DESC));
                holder.ActivityView.setText(activityTypeDesc);
                holder.ValueView.setText(String.valueOf(valueDecimal) + " kg");
                break;
            case 2: // Food
                holder.ActivityView.setText(mCursor.getString(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_FOOD_NAME)));
                holder.ValueView.setText(String.valueOf(Math.round(valueDecimal)) + " g");
                switch (ind1) {
                    case 1:
                        holder.ActivityView.setTextColor(mContext.getResources().getColor(R.color.green));
                        break;
                    case 2:
                        holder.ActivityView.setTextColor(mContext.getResources().getColor(R.color.yellow));
                        break;
                    case 3:
                        holder.ActivityView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                        break;
                    default:
                }
                break;
            case 3: // Sleep
                holder.ActivityView.setText(mCursor.getString(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_VALUE_TEXT)));
                if (mCursor.getString(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_VALUE_TEXT)).equals(TEXT_VALUE_SLEEP)) {
                    holder.activityTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sleep_black_24dp));
                } else {
                    holder.activityTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_wake_up_24dp));
                }
                //holder.ValueView.setText(String.valueOf(Math.round(valueDecimal)) + " g");
                break;
            default:
        }

        holder.timeView.setText(time);
        holder.dayView.setText(day);
        holder.hintView.setText(hintText);
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
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            //String activityUUID = mCursor.getString(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_ACTIVITY_ID));
            int rowID = mCursor.getInt(mCursor.getColumnIndex(SlimContract.SlimDB._ID));
            int typeID = mCursor.getInt(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_ID));
            mClickHandler.onClick(rowID, typeID);
        }
    }
}
