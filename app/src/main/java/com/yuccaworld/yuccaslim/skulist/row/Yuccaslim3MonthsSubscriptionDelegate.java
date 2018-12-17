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
package com.yuccaworld.yuccaslim.skulist.row;

import com.android.billingclient.api.BillingClient.SkuType;
import com.yuccaworld.yuccaslim.R;
import com.yuccaworld.yuccaslim.billing.BillingProvider;

import java.util.ArrayList;

/**
 * Handles Ui specific to "monthly gas" - subscription row
 */
public class Yuccaslim3MonthsSubscriptionDelegate extends UiManagingDelegate {
    public static final String SKU_ID = "yuccaslim_3_months_subscription";

    public Yuccaslim3MonthsSubscriptionDelegate(BillingProvider billingProvider) {
        super(billingProvider);
    }

    @Override
    public @SkuType
    String getType() {
        return SkuType.SUBS;
    }

    @Override
    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        super.onBindViewHolder(data, holder);
        if (mBillingProvider.is3MonthsSubscribed()) {
            holder.button.setText(R.string.button_own);
        } else {
            holder.button.setText(R.string.button_buy);
        }
        // TODO change the icon
        holder.skuIcon.setImageResource(R.drawable.ic_yuccaslim_logo_24);
    }

    @Override
    public void onButtonClicked(SkuRowData data) {
         mBillingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                    data.getSkuType());
    }
}

