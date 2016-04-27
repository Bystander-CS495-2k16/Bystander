/**************
* Brian Burns
* Amy Puente
* Amy Chockley
* Bystander
* Signs the user into Google Services
* SignInActivity.java
**************/

package com.cs495.bystander;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

/**
 * Sign in logic for Google Services
 */
public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    // State variables
    private GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    SignInButton signIn_btn;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SettingsActivity";
    private String TOKEN;
    SharedPreferences prefs;

    /**
     * Gets preferences, displays appropriate screen
     * @param savedInstanceState The last known activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get whether the user is signed in
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isSignedIn = prefs.getBoolean("signedin", false);

        // Connect Google services
        buildNewGoogleApiClient();
        setContentView(R.layout.activity_sign_in);
        customizeSignBtn();
        setBtnClickListeners();

        // If the user is already signed in
        if (isSignedIn) {
            signIn_btn = (SignInButton) findViewById(R.id.sign_in_button);
            // Get the user name and email
            String user = prefs.getString("user", "");
            String email = prefs.getString("email", "");
            TextView user_name = (TextView) findViewById(R.id.userName);
            TextView email_id = (TextView) findViewById(R.id.emailId);
            user_name.setText(getString(R.string.user_name, user));
            email_id.setText(getString(R.string.user_email, email));

            // Show the sign out screen
            updateUI(true);
        }
        else {
            // Show the sign in screen
            updateUI(false);
        }
    }

    /**
     * Initializes the Google sign in button
     */
    private void buildNewGoogleApiClient() {

        // Connect to Google Services
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Get the API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * Sets listeners for the Google sign in button
     */
    private void setBtnClickListeners() {
        // Button listeners
        signIn_btn.setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

    }

    /**
     * Sets the layout for the Google sign in button
     */
    private void customizeSignBtn() {

        signIn_btn = (SignInButton) findViewById(R.id.sign_in_button);
        signIn_btn.setSize(SignInButton.SIZE_STANDARD);
        signIn_btn.setScopes(gso.getScopeArray());

    }

    /**
     * Run when the activity enters back into view
     */
    protected void onStart() {
        super.onStart();
        // Connect the Google API
        mGoogleApiClient.connect();
    }

    /**
     * Run when the activity exits view
     */
    protected void onStop() {
        super.onStop();
        // Disconnect the Google API
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Called when the sign in or sign out button is clicked
     * @param v The view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Get the button
            case R.id.sign_in_button:
                // Sign in
                signIn();
                break;
            case R.id.sign_out_button:
                // Sign out
                Toast.makeText(this, "Signing out of Google", Toast.LENGTH_SHORT).show();
                signOut();
                break;
        }
    }

    /**
     * Signs the user into Google services
     */
    private void signIn() {
        // Start a Google sign in activity
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Signs the user out of Google services
     */
    private void signOut() {
        // Remove user preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("signedin", false).apply();
        prefs.edit().putString("user", null).apply();
        prefs.edit().putString("email", null).apply();
        // Sign the user out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // Show the sign in activity
                        updateUI(false);

                    }
                });
    }

    /**
     * Called when the Google sign in activity returns
     * @param requestCode Which activity has completed
     * @param resultCode Result of the completed activity
     * @param data The intent from the activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // User signed in
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     * Handles the Google sign in result
     * @param result The result of the sign in attempt
     */
    private void handleSignInResult(GoogleSignInResult result) {
        // Log the sign in attempt
        Log.d(TAG, "handleSignInResult: " + result.isSuccess());
        if (result.isSuccess()) {
            // Get the user account
            GoogleSignInAccount acct = result.getSignInAccount();
            // Get the state TextViews
            TextView user_name = (TextView) findViewById(R.id.userName);
            TextView email_id = (TextView) findViewById(R.id.emailId);
            try {
                // Make sure we have a valid account
                if (acct != null && acct.getDisplayName() != null && acct.getEmail() != null) {
                    // Get the account details
                    user_name.setText(getString(R.string.user_name, acct.getDisplayName()));
                    email_id.setText(getString(R.string.user_email, acct.getEmail()));
                    // Get the account's OAuth token
                    new GetToken(this, acct.getEmail()).execute();
                    // Store settings
                    prefs.edit().putBoolean("signedin", true).apply();
                    prefs.edit().putString("user", acct.getDisplayName()).apply();
                    prefs.edit().putString("email", acct.getEmail()).apply();
                    // Store a good token
                    if (TOKEN != null) {
                        prefs = PreferenceManager.getDefaultSharedPreferences(this);
                        prefs.edit().putString("oauth", TOKEN).apply();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Show the sign out screen
            updateUI(true);
        } else {
            // Sign in failed, show the sign in screen
            updateUI(false);
        }
    }

    /**
     * Sets the layout depending on the sign in status
     * @param signedIn 'true' if user is signed into Google, 'false' if not
     */
    private void updateUI(boolean signedIn) {
        if (signedIn) {
            // Show the sign out screen
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            // Show the sign in screen
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    /**
     * If the Google API is unable to connect
     * @param connectionResult The result of the connection attempt
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /******** Asynchronous task to get Oauth token ********/

    /**
     * Gets an OAuth 2.0 token for a logged in user
     */
    public class GetToken extends AsyncTask<Void, Void, Void> {
        Context mActivity; // changed this to context from example code's activity type
        String username;

        /**
         * Constructor
         * @param activity The activity this object was initialized in
         * @param name The email of the logged in user
         */
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
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Gets an authentication token from Google and handles any
         * GoogleAuthException that may o
         * ccur.
         * @return the token of the user
         */
        protected String fetchToken() throws IOException {
            try {
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

        /**
         * Handles an exception while retrieving a token
         * @param e The exception to handle
         */
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
