package com.cs495.bystander;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.Preference;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class Settings extends FragmentActivity {

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


    public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref);
            Preference signinpref = (Preference) findPreference("signin");
            signinpref.setOnPreferenceClickListener(this);
        }

        /* ------------------ GOOGLE AUTH ------------------ */
        public boolean onPreferenceClick(Preference preference) {
            SignInActivity signin = new SignInActivity();
            signin.pickAccount(this.getActivity());
//            Activity current = SettingsFragment.this.getActivity();
//            Intent intent = new Intent(current, SignInActivity.class);
//            current.startActivity(intent);
            return true;
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            System.out.println("SLKJSF:DKJS:LKJ:LSKJ:LKFSJ");
            System.out.println("BOOOOOOOOOOOOOOOP");
            if (key.equals("State")) {
                Log.d("Settings", "STATE");
                Preference connectionPref = findPreference(key);
            }
        }
    }
}
