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

public class Settings extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.getAll();
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


    public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref);
            Preference signinpref = findPreference("signin");
            signinpref.setOnPreferenceClickListener(this);
        }

        /* ------------------ GOOGLE AUTH ------------------ */
        public boolean onPreferenceClick(Preference preference) {
            Activity current = SettingsFragment.this.getActivity();
            Intent intent = new Intent(current, SignInActivity.class);
            current.startActivity(intent);
            return true;
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            if (key.equals("State")) {
                Log.d("Settings", "STATE");
            }
        }
    }
}
