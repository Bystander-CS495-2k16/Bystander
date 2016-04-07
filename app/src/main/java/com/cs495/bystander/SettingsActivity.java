package com.cs495.bystander;


import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import android.preference.SwitchPreference;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import android.preference.Preference.OnPreferenceClickListener;

import static android.preference.Preference.*;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    public static String gmailAccount;
    public static String state;
    public static boolean upload;
    public static boolean isPrivate;
    private static Activity activity;
    public static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        activity = SettingsActivity.this;
        db = MainActivity.getDb();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void addTuple(String email, String token, String State, boolean broadcastOn, String privacySetting) {
        MainActivity.db.execSQL("insert into UserSettings (email, broadcastOn, privacySetting, state, token) values " + "(" + email + ", '" + broadcastOn + "', '" + privacySetting + ", " + state + ", " + token +  " ON DUPLICATE KEY UPDATE broadcastOn=" + broadcastOn + ", privacySetting=" + privacySetting + ", state=" + state  + "');");
        /*
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "UserSettings"
                    + " ( email TEXT PRIMARY KEY, broadcastOn BOOLEAN, privacySetting TEXT, state TEXT, token TEXT);");
         */
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            for (int i = 0; i < 10; i++) {
                System.out.println("preference key " + preference.getKey());
            }
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                System.out.println("INDEX " + index);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
                if (preference.getKey().toString() == "broadcastprivacy") {
                    System.out.println("broadcast privacy " + preference.getSummary());
                    if (preference.getSummary().toString() == "Private") {
                        isPrivate = true;
                    } else {
                        isPrivate = false;
                    }
                } else {
                    System.out.println("state hey " + preference.getKey());
                    state = preference.getSummary().toString();
                }
                System.out.println("setting preference " + preference.getSummary());
                state = preference.getSummary().toString();

            } else if (preference instanceof EditTextPreference) {
                System.out.println("HEAYAYYAYAYAY");
                pickAccount(activity);
            } else if (preference instanceof SwitchPreference) {
                System.out.println("pref " + preference.getSummary());

            } if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }


    };

    private OnPreferenceClickListener newLilClickListener = new OnPreferenceClickListener () {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            System.out.println("HAHAAHHHAHA");
            pickAccount(SettingsActivity.this);
            return true;
        }


    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("add_gmail"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
            bindPreferenceSummaryToValue(findPreference("broadcastprivacy"));
            //newLilClickListener.onPreferenceClick(findPreference("addgmailpreference"));
            System.out.println("preference !!!! " + findPreference("addgmailpreference").getKey());
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /************************* very nasty getting auth token stuff **************************/
        private static String TOKEN;
        String SCOPE = "oauth2:https://www.googleapis.com/auth/youtube.force-ssl";

        static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

        public static void pickAccount(Activity activity) {

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

        String mEmail; // Received from newChooseAccountIntent(); passed to getToken()


        /**
         * Attempts to retrieve the username.
         * If the account is not yet known, invoke the picker. Once the account is known,
         * start an instance of the AsyncTask to get the auth token and do work with it.
         */
        private void getUsername() {
            for (int i = 0; i < 15; i++) {
                System.out.println("iiii");
            }

            if (mEmail == null) {
                for (int i = 0; i < 15; i++) {
                    System.out.println("jjjj");
                }

            } else {
                if (isDeviceOnline(this)) {
                    for (int i = 0; i < 15; i++) {
                        System.out.println("hey");
                    }
                    new GetUsernameTask(SettingsActivity.this, mEmail, SCOPE).execute();
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
                    for (int i = 0; i < 15; i++) {
                        System.out.println("TOKENANNNNNN " + TOKEN);
                    }
                    /*youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) throws IOException {}}).setApplicationName("VideoRecorder").build();
                            // The fetchToken() method handles Google-specific exceptions,*/
                    // so this indicates something went, new  wrong at a higher level.
                    // TIP: Check for network connectivity before starting the AsyncTask.
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
                                    SettingsActivity.this,
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
