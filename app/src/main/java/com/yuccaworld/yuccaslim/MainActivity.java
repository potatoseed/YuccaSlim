package com.yuccaworld.yuccaslim;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuccaworld.yuccaslim.data.AppDatabase;
import com.yuccaworld.yuccaslim.model.Activity;
import com.yuccaworld.yuccaslim.model.Daily;
import com.yuccaworld.yuccaslim.model.FoodFavor;
import com.yuccaworld.yuccaslim.skulist.AcquireFragment;
import com.yuccaworld.yuccaslim.utilities.AppExecutors;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppActivity {
    private static final String TAG = "YSMainActivity";
    // Tag for a dialog that allows us to find it when screen was rotated

    private static final int RC_SIGN_IN = 333;
    public static final int RC_REGISRATION = 1516;
    private static final String REG_DIALOG_TAG = "registration_fragment";
    private static final int RC_GUIDE = 1555 ;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDB;
    private ValueEventListener mValueEventListener;
    private AppDatabase mDb;
    private String mMode="";
    private String mPurcahseResult="FAIL";
    private RegisterationFragment mRegistrationFragment;
    private TextView mLoginMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDb = AppDatabase.getInstance(getApplicationContext());
        // Set Firebase DB log level for debug purpose
        //FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        mFirebaseDB = FirebaseDatabase.getInstance().getReference();
        intstantiateUser();
        //Intent intent = new Intent(MainActivity.this, RegisterationActivity.class);
        //Intent intent = new Intent(MainActivity.this, WeightActivity.cl  ass);
        //startActivity(intent);

        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            mMode = intent.getStringExtra(Intent.EXTRA_TEXT);
            mPurcahseResult = intent.getStringExtra(SlimUtils.EXTRA_PURCHASE_RESULT);
        }

        setupInitalView();
/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/



        // Specify purchase and drive buttons listeners
        Button buttonJoin = findViewById(R.id.buttonJoin);
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onJoinButtonClicked(view);
            }
        });

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFirebaseUser == null) {
                    signIn();
                } else {
                    setupInitalView();
                }
            }
        });
    }

    private void onJoinButtonClicked(final View view) {
        Log.d(TAG, "Join button clicked.");
        Intent registerIntent = new Intent(MainActivity.this, UserRegistrationActivity.class);
//        startActivity(registerIntent);
        startActivityForResult(registerIntent, RC_REGISRATION);
    }

    private boolean isRegisterationFragmentShown() {
        return mRegistrationFragment != null && mRegistrationFragment.isVisible();
    }


    private void setupInitalView() {
        mLoginMessage = findViewById(R.id.textViewLoginMessage);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean bShowGuideOnStart = sharedPreferences.getBoolean(getString(R.string.key_show_guide_on_start),true);
        if (mFirebaseUser != null) {
            // user is sign in
            Toast.makeText(MainActivity.this, "you are now singed in",
                    Toast.LENGTH_SHORT).show();
//            if (b == false && !mMode.equals("USER")) {
            if (bShowGuideOnStart == false) {
                Intent todayIntent = new Intent(MainActivity.this, TodayActivity.class);
                startActivity(todayIntent);
            } else {
                Intent guideIntent = new Intent(MainActivity.this, GuideActivity.class);
                startActivityForResult(guideIntent, RC_GUIDE);
            }
        } else {
//            signIn();
            //signUp();
            if (mPurcahseResult == "OK"){
                signIn();
            }
        }
    }

    private void RestoreActivityFromCloud(String gUid) {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Activity activity = ds.getValue(Activity.class);
                            int i = mDb.activityDao().updateActivity(activity);
                            if(i==0) {
                                long l = mDb.activityDao().insertActivity(activity);
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mFirebaseDB.child("Activity").child(SlimUtils.gUid).addListenerForSingleValueEvent(mValueEventListener);

        ValueEventListener valueEventListenerDaily = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            Daily daily = ds.getValue(Daily.class);
                            int i;
                            i = mDb.dailyDao().deleteDailyByDate(daily.getDate());
                            long l = mDb.dailyDao().insertDaily(daily);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mFirebaseDB.child("Daily").child(SlimUtils.gUid).addListenerForSingleValueEvent(valueEventListenerDaily);

        ValueEventListener valueEventListenerFoodFavor = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            FoodFavor foodFavor = ds.getValue(FoodFavor.class);
                            int i = mDb.FoodFavorDao().UpdateFoodFavorr(foodFavor);
                            if (i==0) {
                                long l = mDb.FoodFavorDao().insertFoodFavor(foodFavor);
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mFirebaseDB.child("FoodFavor").child(SlimUtils.gUid).addListenerForSingleValueEvent(valueEventListenerFoodFavor);
    }

    private void signUp() {
        mFirebaseAuth.createUserWithEmailAndPassword("a@b.com", "123456")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignUp", "createUserWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
//        selectedProviders.add(
//                new AuthUI.IdpConfig.GoogleBuilder().setScopes(getGoogleScopes())
//                        .build());

//      selectedProviders.add(new AuthUI.IdpConfig.FacebookBuilder()
//                    .setPermissions(getFacebookPermissions())
//                    .build());

//      selectedProviders.add(new AuthUI.IdpConfig.TwitterBuilder().build());

        selectedProviders.add(new AuthUI.IdpConfig.EmailBuilder()
                .setRequireName(false)
                .setAllowNewAccounts(false)
                .build());

//        selectedProviders.add(new AuthUI.IdpConfig.PhoneBuilder().build());

        return selectedProviders;
    }

    public void signIn() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
//                        .setTheme(getSelectedTheme())
//                        .setLogo(getSelectedLogo())
                        .setAvailableProviders(getSelectedProviders())
//                        .setTosUrl(getSelectedTosUrl())
//                        .setPrivacyPolicyUrl(getSelectedPrivacyPolicyUrl())
                        .setIsSmartLockEnabled(false,
                                true)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (RC_SIGN_IN) :
                handleSignInResponse(resultCode, data);
                break;
            case RC_REGISRATION:
                if (data != null) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.message_purchase_successful, Snackbar.LENGTH_LONG).show();
                    mLoginMessage.setVisibility(View.VISIBLE);
                    mLoginMessage.setText(R.string.message_registration_successful);
//                    mLoginMessage.setTextColor(getResources().getColor(R.color.browser_actions_text_color));
                    mLoginMessage.setTextColor(getResources().getColor(R.color.browser_actions_text_color));
                    mPurcahseResult = data.getStringExtra(SlimUtils.EXTRA_PURCHASE_RESULT);
                    if ("OK".equals(mPurcahseResult)) {
                        signIn();
                    }
                }
                break;
            case RC_GUIDE:
                Intent todayIntent = new Intent(MainActivity.this, TodayActivity.class);
                startActivity(todayIntent);
                break;
        }

    }

    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            SlimUtils.gUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // Restore data from firebase DB
            RestoreActivityFromCloud(SlimUtils.gUid);

            Intent intent = new Intent(MainActivity.this, TodayActivity.class);
            //Intent intent = new Intent(MainActivity.this, FoodSearchActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                Toast.makeText(MainActivity.this, "Sign in Cancelled",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(MainActivity.this, "No Network",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(MainActivity.this, "Unknown Error",
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Sign-in error: ", response.getError());
        }
    }



    private List<String> getGoogleScopes() {
        List<String> result = new ArrayList<>();
        result.add("https://www.googleapis.com/auth/youtube.readonly");
        result.add(Scopes.DRIVE_FILE);
        return result;
    }

    private void intstantiateUser() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Show an alert dialog to the user
     * @param messageId String id to display inside the alert dialog
     * @param optionalParam Optional attribute for the string
     */
    @UiThread
    void alert(@StringRes int messageId, @Nullable Object optionalParam) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("Dialog could be shown only from the main thread");
        }

        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setPositiveButton("OK", null);

        if (optionalParam == null) {
            bld.setMessage(messageId);
        } else {
            bld.setMessage(getResources().getString(messageId, optionalParam));
        }

        bld.create().show();
    }
}
