package com.yuccaworld.yuccaslim;


import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.yuccaworld.yuccaslim.data.SlimContract;


public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.FoodSearchViewHolder> {
    private final Context mContext;
    private Cursor mCursor;
    private int mNumberItems = 20;


    public FoodSearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FoodSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.food_list_item, parent, false);
        return new FoodSearchViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FoodSearchViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.foodNameView.setText(mCursor.getString(mCursor.getColumnIndex(SlimContract.SlimDB.COLUMN_FOOD_NAME)));

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    class FoodSearchViewHolder extends RecyclerView.ViewHolder{
        final TextView foodNameView;
        final EditText foodQtyEditText;
        final ImageView addToMealImage;

        public FoodSearchViewHolder(View itemView) {
            super(itemView);
            foodNameView = (TextView) itemView.findViewById(R.id.textViewFoodName);
            foodQtyEditText = (EditText) itemView.findViewById(R.id.editTextFoodQty);
            addToMealImage = (ImageView) itemView.findViewById(R.id.imageViewAddToMeal);
        }
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
}
