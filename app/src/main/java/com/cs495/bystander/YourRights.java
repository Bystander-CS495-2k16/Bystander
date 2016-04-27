package com.cs495.bystander;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class YourRights extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, android.location.LocationListener {

    private GoogleApiClient mGoogleApiClient;
    SharedPreferences prefs;
    int PERMISSION_CLOCATION;
    int PERMISSION_FLOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_rights);

        // Set up Google Services
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(LocationServices.API)
                    .build();
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pref_example_list_titles, android.R.layout.simple_spinner_dropdown_item);

        final TextView stateName = (TextView) findViewById(R.id.stateTextView);
        stateName.setText(adapter.getItem(Integer.parseInt(getState())));
        final TextView rightType = (TextView) findViewById(R.id.typeTextView);
        rightType.setText(getRightsType(getRightsCodeFromDB(getState())));

        final TextView rights = (TextView)findViewById(R.id.rightsextView);
        // set the text to be the user's rights, by querying the db using the state preference
        rights.setText(getRights(getRightsCodeFromDB(getState())));

        // Initialize location button
        Button locButton = (Button) findViewById(R.id.locButton);
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location loc = getLocation();
                if (loc != null) {
                    try {
                        Geocoder geocoder = new Geocoder(YourRights.this, Locale.getDefault());
                        List<Address> addresses;
                        addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                        String state = addresses.get(0).getAdminArea();
                        Toast.makeText(YourRights.this, state, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Error getting location.");
                    }
                } else {
                    Toast.makeText(YourRights.this, "Could not get location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.stateSpinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(Integer.parseInt(prefs.getString("State", null)));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateName.setText(adapter.getItem(parent.getSelectedItemPosition()));
                rightType.setText(getRightsType(getRightsCodeFromDB(Integer.toString(parent.getSelectedItemPosition()))));
                String item = parent.getItemAtPosition(position).toString();
                rights.setText(getRights(getRightsCodeFromDB(Integer.toString(parent.getSelectedItemPosition()))));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    // gets State from settings, returns null if not found
    protected String getState(){
        System.out.println("state " + prefs.getString("State", null));
        return prefs.getString("State", null);
    }

    // get the rights from the database and return them, returns default string if not found
    protected String getRightsCodeFromDB(String state) {
        if (state != null) { // check if the state was actually retrieved
            System.out.println("state again " + state);
            Cursor curs = MainActivity.db.rawQuery("select rights from Rights where state = " + state + ";", null);
            curs.moveToFirst();
            System.out.println("before while loop");
            System.out.println("curs is after last " + curs.isAfterLast());
            while (curs.isAfterLast() == false) {
                System.out.println("state rights " + curs.getString(curs.getColumnIndex("rights")));
                return curs.getString(curs.getColumnIndex("rights")); // return the rights
            }
        }
        return "not found";
    }

    // takes the output from a query and matches the code with the type of rights associated with it
    protected String getRights(String rightType) {
        switch (rightType) {
            case "1pc":
                return "This is a 1-party consent state. This means one party must consent to the recording.";
            case "2pc":
                return "This is a 2-party consent state. This means both parties must consent to recording. ";
            case "none":
                return "The laws are not straightforward here. Use caution if you choose to record!";
            case "priv":
                return "This is a 1-party consent state unless the recording is done in private. If it's private both parties must consent to the recording.";
            case "noti":
                return "You must notify all parties that they are being recorded here. ";
            default:
                return "Couldn't find your state's rights. Please check your current settings";
        }
    }

    protected String getRightsType(String rightCode) {
        switch (rightCode) {
            case "1pc":
                return "1-Party Consent";
            case "2pc":
                return "2-Party Consent";
            case "none":
                return "Unknown";
            case "priv":
                return "1-Party Consent with Privacy Clause";
            case "noti":
                return "Notification";
            default:
                return "Not Found";
        }
    }

    private Location getLocation() {
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(YourRights.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(YourRights.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CLOCATION);
            }
            permissionCheck = ContextCompat.checkSelfPermission(YourRights.this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(YourRights.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FLOCATION);
            }

            LocationManager locManager = (LocationManager) this.getBaseContext().getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                return null;
            } else {
                if (isNetworkEnabled) {
                    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, (float) 50.0, this);
                    return locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (isGPSEnabled) {
                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, (float) 50.0, this);
                    return locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("GETLOCATION: could not get location");
        }
        return null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
