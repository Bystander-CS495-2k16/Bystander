/**************
* Brian Burns
* Amy Puente
* Amy Chockley
* Bystander
* The settings activity
* Settings.java
**************/

package com.cs495.bystander;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.Preference;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * The settings activity
 */
public class Settings extends FragmentActivity {

   /**
    * Creates the settings activity, retrieves settings
    * @param savedInstanceState The last known activity state
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.getAll();
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

   /**
    * The settings to show on the activity
    */
    public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
      /**
       * Gets the shared preferences
       * @param savedInstanceState The last known activity state
       */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref);
            Preference signinpref = findPreference("signin");
            signinpref.setOnPreferenceClickListener(this);
        }

       /**
        * Called when the user clicks to manage their account
        * @param preference The preference clicked
        * @return 'true' if SignInActivity started, 'false' if not
        */
        public boolean onPreferenceClick(Preference preference) {
            // Go to the sign in page
            Activity current = SettingsFragment.this.getActivity();
            Intent intent = new Intent(current, SignInActivity.class);
            current.startActivity(intent);
            return true;
        }

       /**
        * Shows when the user changes their state
        * @param sharedPreferences The shared preferences
        * @param key Whether the user changed their state
        */
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
        String key) {
            if (key.equals("State")) {
                // Log the change
                Log.d("Settings", "STATE");
            }
        }
    }
}
