package com.yuccaworld.yuccaslim;

import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.yuccaworld.yuccaslim.billing.BillingManager;
import com.yuccaworld.yuccaslim.skulist.row.Yuccaslim3MonthsSubscriptionDelegate;

import java.util.List;

public class MainViewController {
    private static final String TAG = "MainViewController";
    private final UpdateListener mUpdateListener;
    private MainActivity mActivity;
    // Tracks if we currently own subscriptions SKUs
    private boolean m3MonthsSubscription;
    public boolean is3MonthsSubscription(){return m3MonthsSubscription;}

    public MainViewController(MainActivity mActivity) {
        this.mUpdateListener = new UpdateListener();
        this.mActivity = mActivity;
    }

    public BillingManager.BillingUpdatesListener getUpdateListener() {
        return mUpdateListener;
    }

    private class UpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {

        }

        @Override
        public void onConsumeFinished(String token, int result) {

        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            m3MonthsSubscription = false;
            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    case Yuccaslim3MonthsSubscriptionDelegate.SKU_ID:
                        m3MonthsSubscription = true;
                    break;
                }
            }
            mActivity.showRefreshedUi();
        }
    }
}
