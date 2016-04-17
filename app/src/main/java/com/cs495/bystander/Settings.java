package com.cs495.bystander;

import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.Preference;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.Context;

public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println("prefs to string " + sharedPref.toString());
        SharedPreferences.Editor editor = sharedPref.edit();
        sharedPref.getAll();
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


    public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref);

        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            System.out.println("SLKJSF:DKJS:LKJ:LSKJ:LKFSJ");
            System.out.println("BOOOOOOOOOOOOOOOP");
            if (key.equals("State")) {
                Preference connectionPref = findPreference(key);
                // Set summary to be the user-description for the selected value
                System.out.println("STATEEEEEE");
                System.out.println("SUMMMARY " + connectionPref.getSummary());
               // connectionPref.setSummary(sharedPreferences.getString(key, ""));
            }
        }

    }
}
