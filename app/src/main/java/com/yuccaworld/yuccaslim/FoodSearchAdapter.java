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

import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.model.Food;

import java.util.List;


public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.FoodSearchViewHolder> {
    private final Context mContext;
    protected Cursor mCursor;
    private int mNumberItems = 20;
    final private FoodItemClickListerner mOnClickListerner;
    private List<Food> mFoodList;


    public FoodSearchAdapter(Context mContext, FoodItemClickListerner listerner) {
        this.mContext = mContext;
        mOnClickListerner = listerner;
    }
    public interface FoodItemClickListerner {
        void onItemClick(int clickedPosition, int foodId, int recordID);
    }

    @NonNull
    @Override
    public FoodSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.food_list_item, parent, false);
        return new FoodSearchViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FoodSearchViewHolder holder, int position) {
        Food food = mFoodList.get(position);
        holder.foodNameView.setText(food.getFoodName());
        int foodId = food.getFoodId();
        holder.foodIDview.setText(Integer.toString(foodId));
        holder.recordIDview.setText(Integer.toString(food.getId()));
    }

    @Override
    public int getItemCount() {
        if (mFoodList == null) {
            return 0;
        }
        return mFoodList.size();
    }

    class FoodSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView foodNameView;
        final TextView foodIDview;
        final TextView recordIDview;
        final EditText foodQtyEditText;
        final ImageView addToMealImage;

        public FoodSearchViewHolder(View itemView) {
            super(itemView);
            foodNameView = (TextView) itemView.findViewById(R.id.textViewFoodName);
            foodQtyEditText = (EditText) itemView.findViewById(R.id.editTextFoodQty);
            addToMealImage = (ImageView) itemView.findViewById(R.id.imageViewAddToMeal);
            foodIDview = itemView.findViewById(R.id.textViewFoodID);
            recordIDview = itemView.findViewById(R.id.textViewRecordID);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Food food = mFoodList.get(clickedPosition);
            int foodId = food.getFoodId();
            int recordID = food.getId();
            mOnClickListerner.onItemClick(clickedPosition, foodId, recordID);
        }
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setFoodList(List<Food> foodList) {
        mFoodList = foodList;
        notifyDataSetChanged();
    }

    /**
     * When data changes and a re-query occurs, this function update the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */

}
