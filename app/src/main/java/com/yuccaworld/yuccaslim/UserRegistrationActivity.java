package com.yuccaworld.yuccaslim;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.mobsandgeeks.saripaar.annotation.ConfirmEmail;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.yuccaworld.yuccaslim.billing.BillingManager;
import com.yuccaworld.yuccaslim.billing.BillingProvider;
import com.yuccaworld.yuccaslim.billing.SubscriptionStatus;
import com.yuccaworld.yuccaslim.skulist.AcquireFragment;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yuccaworld.yuccaslim.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED;

public class UserRegistrationActivity extends AppActivity implements BillingProvider {
    private static final String TAG = "MainActivity";
    @Email(messageResId = R.string.email_validation)
    private EditText Email;

    @ConfirmEmail
    private EditText confirmEmail;

    @Password(min = 6, scheme = Password.Scheme.NUMERIC, messageResId = R.string.validation_password_number)
    private EditText passwordInput;

    @ConfirmPassword
    private EditText passwordConfirm;

    private Button purchaseButton;
    private AcquireFragment mAcquireFragment;
    private BillingManager mBillingManager;
    private MainViewController mViewController;
    private View mScreenWait, mScreenMain;
    private static final String DIALOG_TAG = "dialog";
    private View mMainLayout;
    private TextView mRegistrationStatusMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration);
        // Billing section
        // Start the controller and load data
        mViewController = new MainViewController(this);

        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, mViewController.getUpdateListener());
        mScreenWait = findViewById(R.id.screen_wait);
        mScreenMain = findViewById(R.id.screen_main);

        initView();
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
                if (validated){
                    SlimUtils.gUserRegistrationEmail = Email.getText().toString().trim();
                    SlimUtils.gRegistrationPassword = passwordInput.getText().toString().trim();
                    OnPurchaseButtonClick();

                }
            }
        });
    }

    private void OnPurchaseButtonClick() {
        Log.d(TAG, "Purchase button clicked.");

        if (mAcquireFragment == null) {
            mAcquireFragment = new AcquireFragment();
        }

        if (!isAcquireFragmentShown()) {
            mAcquireFragment.show(getSupportFragmentManager(), DIALOG_TAG);
            if (mBillingManager != null
                    && mBillingManager.getBillingClientResponseCode()
                    > BILLING_MANAGER_NOT_INITIALIZED) {
                mAcquireFragment.onManagerReady(this);
            }
        }
    }

    private void initView() {
        Email = findViewById(R.id.editTextMail);
        confirmEmail = findViewById(R.id.editTextConfrimEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        passwordConfirm = findViewById(R.id.editTextPasswordConfirm);
        purchaseButton = findViewById(R.id.buttonPurchase);
        mMainLayout = findViewById(R.id.registrationMainLayout);
        mRegistrationStatusMessage = findViewById(R.id.textViewRegistrationMessage);
        if (!SlimUtils.gUserRegistrationEmail.isEmpty()){
            Email.setText(SlimUtils.gUserRegistrationEmail);
            confirmEmail.setText(SlimUtils.gUserRegistrationEmail);

        }
        if (!SlimUtils.gRegistrationPassword.isEmpty()) {
            passwordInput.setText(SlimUtils.gRegistrationPassword);
            passwordConfirm.setText(SlimUtils.gRegistrationPassword);
        }
    }

    private boolean isAcquireFragmentShown() {
        return mAcquireFragment != null && mAcquireFragment.isVisible();
    }

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    public void showRefreshedUi() {
        setWaitScreen(false);
        updateUi();
        if (mAcquireFragment != null) {
            mAcquireFragment.refreshUI();
        }
    }

    /**
     * Update UI to reflect model
     */
    @UiThread
    private void updateUi() {
        Log.d(TAG, "Updating the UI. Thread: " + Thread.currentThread().getName());

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.
        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases();
        }
    }

    private void setWaitScreen(boolean set) {
        mScreenMain.setVisibility(set ? View.GONE : View.VISIBLE);
        mScreenWait.setVisibility(set ? View.VISIBLE : View.GONE);
    }

    void onBillingManagerSetupFinished() {
        if (mAcquireFragment != null) {
            mAcquireFragment.onManagerReady(this);
        }
    }

    @Override
    public boolean is3MonthsSubscribed() {
        return mViewController.is3MonthsSubscribed();
    }

    public void onPurchaseSuccess(Purchase purchase) {
//        alert(R.string.message_purchase_successful,null);

//        Intent purchaseIntent = new Intent(UserRegistrationActivity.this, MainActivity.class);

        if (mAcquireFragment != null) {
            mAcquireFragment.dismiss();
        }
        mRegistrationStatusMessage.setVisibility(View.VISIBLE);
        mRegistrationStatusMessage.setText(R.string.message_purchase_successful);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(R.string.message_purchase_successful)
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
        verifySubscriptionRegistration(purchase);
    }

    private void verifySubscriptionRegistration(Purchase purchase) {
        HashMap data = new HashMap();
        data.put("sku", purchase.getSku());
        data.put("token", purchase.getPurchaseToken());
        FirebaseFunctions.getInstance().getHttpsCallable("verify_subscription_purchase")
                .call(data)
                .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        Snackbar.make(getWindow().getDecorView().getRootView(), "complete firebase reg, Successful?:" + task.isSuccessful() , Snackbar.LENGTH_LONG).show();
                        List resultList;
                        if (task.isSuccessful()){
                            if (task.getResult() != null) {
                                Map result = (Map) task.getResult().getData();
                                if (result != null) {
                                    resultList = SubscriptionStatus.Companion.listFromMap(result);
                                    if (((SubscriptionStatus) resultList.get(0)).isEntitlementActive()) {
                                        mRegistrationStatusMessage.setText(R.string.message_registration_successful);
                                        Intent purchaseIntent = new Intent();
                                        purchaseIntent.putExtra(SlimUtils.EXTRA_PURCHASE_RESULT, "OK");
                                        setResult(Activity.RESULT_OK, purchaseIntent);
                                        finish();
                                    }
                                }
                            }
                        }
                    }
                });
    }
}
