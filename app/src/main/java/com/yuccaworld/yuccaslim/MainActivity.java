package com.yuccaworld.yuccaslim;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.yuccaworld.yuccaslim.data.SlimContract;
import com.yuccaworld.yuccaslim.model.ActivityInfo;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppActivity {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 333;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDB;
    private ValueEventListener mValueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        mFirebaseDB = FirebaseDatabase.getInstance().getReference();
        intstantiateUser();
        //Intent intent = new Intent(MainActivity.this, RegisterationActivity.class);
        //Intent intent = new Intent(MainActivity.this, WeightActivity.class);
        //startActivity(intent);
        setupInitalView();
/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

    }

    private void setupInitalView() {
        if (mFirebaseUser != null) {
            // user is sign in
            Toast.makeText(MainActivity.this, "you are now singed in",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, TodayActivity.class);
            //Intent intent = new Intent(MainActivity.this, SleepActivity.class);
            startActivity(intent);
        } else {
            signIn();

            //signUp();
        }
    }

    private void RestoreActivityFromCloud(String gUid) {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ActivityInfo activityInfo = ds.getValue(ActivityInfo.class);
                    // Check if the data from cloud existing in local DB, if not, insert into local DB.
                    Uri uri = SlimContract.SlimDB.CONTENT_ACTIVITY_URI;
                    uri = uri.buildUpon().appendPath(activityInfo.getActivityID()).build();
//                    Cursor cursor = getContentResolver().query(uri,null,null,null,null);
//                    if(cursor.getCount() == 0) {
//                        ContentValues contentValues;
//                        contentValues = SlimUtils.ConvertActivityContentValue(activityInfo);
//                        Uri uri1 = getContentResolver().insert(SlimContract.SlimDB.CONTENT_ACTIVITY_URI, contentValues);
////                        Toast.makeText(MainActivity.this, "insert in SQLite DB, uri1=" + uri1.toString(),
////                                Toast.LENGTH_SHORT).show();
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mFirebaseDB.child("Activity").child(SlimUtils.gUid).addListenerForSingleValueEvent(mValueEventListener);
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

        selectedProviders.add(new AuthUI.IdpConfig.PhoneBuilder().build());

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
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);

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
}
