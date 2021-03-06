/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuccaworld.yuccaslim.skulist;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.yuccaworld.yuccaslim.skulist.row.RowDataProvider;
import com.yuccaworld.yuccaslim.skulist.row.RowViewHolder;
import com.yuccaworld.yuccaslim.skulist.row.SkuRowData;
import com.yuccaworld.yuccaslim.skulist.row.UiManager;

import java.lang.annotation.Retention;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Adapter for a RecyclerView that shows SKU details for the app.
 * <p>
 *     Note: It's done fragment-specific logic independent and delegates control back to the
 *     specified handler (implemented inside AcquireFragment in this example)
 * </p>
 */
public class SkusAdapter extends RecyclerView.Adapter<RowViewHolder> implements RowDataProvider {
    /**
     * Types for adapter rows
     */
    @Retention(SOURCE)
    @IntDef({TYPE_HEADER, TYPE_NORMAL})
    public @interface RowTypeDef {}
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    private UiManager mUiManager;
    private List<SkuRowData> mListData;

    void setUiManager(UiManager uiManager) {
        mUiManager = uiManager;
    }

    void updateData(List<SkuRowData> data) {
        mListData = data;
        notifyDataSetChanged();
    }

    @Override
    public @RowTypeDef int getItemViewType(int position) {
        return mListData == null ? TYPE_HEADER : mListData.get(position).getRowType();
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, @RowTypeDef int viewType) {
        return mUiManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        mUiManager.onBindViewHolder(getData(position), holder);
    }

    @Override
    public int getItemCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public SkuRowData getData(int position) {
        return mListData == null ? null : mListData.get(position);
    }

//    class RowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        public TextView title, description, price;
//        public Button button;
//        public ImageView skuIcon;
//
//        @Override
//        public void onClick(View view) {
//
//        }
//
//        public RowViewHolder(final View itemView, final com.yuccaworld.yuccaslim.skulist.row.RowViewHolder.OnButtonClickListener clickListener) {
//            super(itemView);
//            title = (TextView) itemView.findViewById(R.id);
//            title = (TextView) itemView.findViewById(R.id.title);
//            price = (TextView) itemView.findViewById(R.id.price);
//            description = (TextView) itemView.findViewById(R.id.description);
//            skuIcon = (ImageView) itemView.findViewById(R.id.sku_icon);
//            button = (Button) itemView.findViewById(R.id.state_button);
//            if (button != null) {
//                button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        clickListener.onButtonClicked(getAdapterPosition());
//                    }
//                });
//            }
//        }
//    }
}

