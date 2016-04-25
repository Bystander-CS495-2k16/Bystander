package com.cs495.bystander;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

public class SignInActivity extends AppCompatActivity {

//    private GoogleApiClient mGoogleApiClient;
//    GoogleSignInOptions gso;
//    SignInButton signIn_btn;
//    private static final int RC_SIGN_IN = 9001;
//    private static final String TAG = "SettingsActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Google sign in
//        buidNewGoogleApiClient();
//        setContentView(R.layout.activity_sign_in);
//        customizeSignBtn();
//        setBtnClickListeners();
//    }
//
//    private void buidNewGoogleApiClient(){
//
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this )
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//    }
//
//    private void setBtnClickListeners(){
//        // Button listeners
//        signIn_btn.setOnClickListener(this);
//        findViewById(R.id.sign_out_button).setOnClickListener(this);
//
//    }
//
//    private void customizeSignBtn(){
//
//        signIn_btn = (SignInButton) findViewById(R.id.sign_in_button);
//        signIn_btn.setSize(SignInButton.SIZE_STANDARD);
//        signIn_btn.setScopes(gso.getScopeArray());
//
//    }
//
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }
//
//    protected void onStop() {
//        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.sign_in_button:
//                signIn();
//                break;
//            case R.id.sign_out_button:
//                Toast.makeText(this, "Signing out of Google", Toast.LENGTH_SHORT).show();
//                signOut();
//                break;
//        }
//    }
//
//    private void signIn() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    private void signOut() {
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//
//                        updateUI(false);
//
//                    }
//                });
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
//    }
//
//    private void handleSignInResult(GoogleSignInResult result) {
//        Log.d(TAG, "handleSignInResult: " + result.isSuccess());
//        if (result.isSuccess()) {
//            GoogleSignInAccount acct = result.getSignInAccount();
//            TextView user_name= (TextView)findViewById(R.id.userName);
//            TextView email_id= (TextView)findViewById(R.id.emailId);
//            user_name.setText("User Name: "+ acct.getDisplayName());
//            email_id.setText("Email: " + acct.getEmail());
//            updateUI(true);
//        } else {
//            updateUI(false);
//        }
//    }
//
//    private void updateUI(boolean signedIn) {
//        if (signedIn) {
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
//        } else {
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//    }

    private static String TOKEN;
    String SCOPE = "oauth2:https://www.googleapis.com/auth/youtube.force-ssl";
    String mEmail; // Received from newChooseAccountIntent(); passed to getToken()

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    public void pickAccount(Activity activity) {

        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);

        activity.startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
        //start(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {

            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {

                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                Toast.makeText(this, mEmail, Toast.LENGTH_SHORT).show();
                // With the account name acquired, go get the auth token
                getUsername();
            } else if (resultCode == RESULT_CANCELED) {

                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, "Woah pick an account", Toast.LENGTH_SHORT).show();
            }
        }
        //
        //Handle the result from exceptions

    }


    /**
     * Attempts to retrieve the username.
     * If the account is not yet known, invoke the picker. Once the account is known,
     * start an instance of the AsyncTask to get the auth token and do work with it.
     */
    private void getUsername() {

        if (mEmail == null) {
            for (int i = 0; i < 15; i++) {
                System.out.println("jjjj");
            }

        } else {
            if (isDeviceOnline(this)) {
                for (int i = 0; i < 15; i++) {
                    System.out.println("hey");
                }
                new GetUsernameTask(SignInActivity.this, mEmail, SCOPE).execute();
            } else {
                Toast.makeText(this, "Your device is not currently online!", Toast.LENGTH_LONG).show();
            }
        }
    }


    public boolean isDeviceOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        }
        return ni.isConnected();
    }

    public void addTokenToDB() {
        MainActivity.db.execSQL("INSERT OR REPLACE INTO tokens (email, token) VALUES (\'" + mEmail+ "\', \'" + TOKEN + "\')");
    }

    public class GetUsernameTask extends AsyncTask<Void, Void, Void> {
        Context mActivity; // changed this to context from example code's activity type
        String mScope;
        String mUsername;

        GetUsernameTask(Context activity, String name, String scope) {
            this.mActivity = activity;
            this.mScope = scope;
            System.out.println(mScope);
            this.mUsername = name;
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                TOKEN = fetchToken();
                    /*for (int i = 0; i < 15; i++) {
                        System.out.println("TOKENANNNNNN " + TOKEN);
                    }*/
                addTokenToDB();
                //MainActivity.db.execSQL("select * from tokens;");
                    /*String output = "";
                    Cursor c = MainActivity.db.rawQuery("SELECT * FROM tokens", null);
                    c.moveToFirst();
                    c.moveToNext(); // skip android_metadata table"insert
                    int i = 0;
                    while (c.isAfterLast() == false) {
                        System.out.println(c.toString());
                        System.out.println("token: " + c.getString( c.getColumnIndex("token")));
                        System.out.println(c.getString(c.getColumnIndex("email")));
                        c.moveToNext();
                        i++;
                    }*/

            } catch (IOException e) {

            }
            return null;
        }

        /**
         * Gets an authentication token from Google and handles any
         * GoogleAuthException that may o
         * ccur.
         */
        protected String fetchToken() throws IOException {
            try {
                System.out.println("m activity " + mUsername + " scope " + mScope);
                return GoogleAuthUtil.getToken(mActivity, mUsername, mScope);
            } catch (UserRecoverableAuthException userRecoverableException) {
                // GooglePlayServices.apk is either old, disabled, or not present
                // so we need to show the user some UI in the activity to recover.
                handleException(userRecoverableException);
            } catch (GoogleAuthException fatalException) {
                // Some other type of unrecoverable exception has occurred.
                // Report and log the error as appropriate for your app.
            }
            return null;
        }

        static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

        public void handleException(final Exception e) {
            // Because this call comes from the AsyncTask, we must ensure that the following
            // code instead executes on the UI thread.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (e instanceof GooglePlayServicesAvailabilityException) {
                        // The Google Play services APK is old, disabled, or not present.
                        // Show a dialog created by Google Play services that allows
                        // the user to update the APK
                        int statusCode = ((GooglePlayServicesAvailabilityException) e)
                                .getConnectionStatusCode();
                        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                                SignInActivity.this,
                                REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                        dialog.show();
                    } else if (e instanceof UserRecoverableAuthException) {
                        // Unable to authenticate, such as when the user has not yet granted
                        // the app access to the account, but the user can fix this.
                        // Forward the user to an activity in Google Play services.
                        Intent intent = ((UserRecoverableAuthException) e).getIntent();
                        startActivityForResult(intent,
                                REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    }
                }
            });
        }

        /**
         * This method is a hook for background threads and async tasks that need to
         * provide the user a response UI when an exception occurs.
         */

    }
}
