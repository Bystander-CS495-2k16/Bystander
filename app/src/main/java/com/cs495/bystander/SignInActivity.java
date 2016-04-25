package com.cs495.bystander;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.GoogleAuthException;

import android.os.AsyncTask;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import android.content.Context;
import android.app.Dialog;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    SignInButton signIn_btn;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SettingsActivity";
    private String TOKEN;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Google sign in
        buidNewGoogleApiClient();
        setContentView(R.layout.activity_sign_in);
        customizeSignBtn();
        setBtnClickListeners();
    }

    private void buidNewGoogleApiClient() {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void setBtnClickListeners() {
        // Button listeners
        signIn_btn.setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

    }

    private void customizeSignBtn() {

        signIn_btn = (SignInButton) findViewById(R.id.sign_in_button);
        signIn_btn.setSize(SignInButton.SIZE_STANDARD);
        signIn_btn.setScopes(gso.getScopeArray());

    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                Toast.makeText(this, "Signing out of Google", Toast.LENGTH_SHORT).show();
                signOut();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        updateUI(false);

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult: " + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            TextView user_name = (TextView) findViewById(R.id.userName);
            TextView email_id = (TextView) findViewById(R.id.emailId);
            user_name.setText("User Name: " + acct.getDisplayName());
            email_id.setText("Email: " + acct.getEmail());
            try {
                new GetToken(this, acct.getEmail()).execute(); // get the token
                System.out.println("SIGN IN: " + TOKEN);
                prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putString("oauth", TOKEN).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateUI(true);
        } else {
            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /******** Asynchronous task to get Oauth token ********/

    public class GetToken extends AsyncTask<Void, Void, Void> {
        Context mActivity; // changed this to context from example code's activity type
        String username;

        GetToken(Context activity, String name) {
            this.mActivity = activity;
            this.username = name;
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                TOKEN = fetchToken();
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
                System.out.println("username " + username);
                return GoogleAuthUtil.getToken(mActivity, username, "oauth2:https://www.googleapis.com/auth/youtube.force-ssl");
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
    }
}