package com.yuccaworld.yuccaslim;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Yung on 9/3/2017.
 */

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.TodayAdapterViewHolder> {

    private final Context mContext;

    public TodayAdapter(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public TodayAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.today_list_item, parent, false);

        return new TodayAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodayAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class TodayAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView hintView;
        final TextView timeView;
        final TextView taskView;
        final ImageView taskIcon;
        final ImageView editIcon;

        public TodayAdapterViewHolder(View itemView) {
            super(itemView);
            hintView = (TextView) itemView.findViewById(R.id.textViewHint);
            timeView = (TextView) itemView.findViewById(R.id.textViewTime);
            taskView = (TextView) itemView.findViewById(R.id.textViewActivity);
            taskIcon = (ImageView) itemView.findViewById(R.id.imageViewActivityType);
            editIcon = (ImageView) itemView.findViewById(R.id.imageViewEdit);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
