/**************
* Brian Burns
* Amy Puente
* Amy Chockley
* Bystander
* Activity to display state rights
* YourRights.java
**************/

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
import android.support.annotation.NonNull;
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
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Displays a state's right to the user
 */
public class YourRights extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, android.location.LocationListener {
    // State variables
    private GoogleApiClient mGoogleApiClient;
    SharedPreferences prefs;
    int PERMISSION_CLOCATION;
    int PERMISSION_FLOCATION;

    /**
     * Gets preferences, sets state information
     * @param savedInstanceState The last known activity state
     */
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

        // Get the preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the state array for the spinner
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pref_example_list_titles, android.R.layout.simple_spinner_dropdown_item);

        // Get the user's state, set to Alabama if not set
        int state;
        if (getState() == null) state = 0;
        else state = Integer.parseInt(getState());

        // Set TextViews
        final TextView stateName = (TextView) findViewById(R.id.stateTextView);
        stateName.setText(adapter.getItem(state));
        final TextView rightType = (TextView) findViewById(R.id.typeTextView);
        rightType.setText(getRightsType(getRightsCodeFromDB(Integer.toString(state))));

        final TextView rights = (TextView)findViewById(R.id.rightsTextView);
        // Set the text to be the user's rights, by querying the db using the state preference
        rights.setText(getRights(getRightsCodeFromDB(Integer.toString(state))));

        // State Spinner
        final Spinner spinner = (Spinner) findViewById(R.id.stateSpinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(Integer.parseInt(prefs.getString("State", "0")));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Change the TextViews when the spinner changes
                stateName.setText(adapter.getItem(parent.getSelectedItemPosition()));
                rightType.setText(getRightsType(getRightsCodeFromDB(Integer.toString(parent.getSelectedItemPosition()))));
                rights.setText(getRights(getRightsCodeFromDB(Integer.toString(parent.getSelectedItemPosition()))));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Initialize location button
        Button locButton = (Button) findViewById(R.id.locButton);
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user's location
                Location loc = getLocation();
                if (loc != null) {
                    try {
                        // Get the state from the lat long
                        Geocoder geocoder = new Geocoder(YourRights.this, Locale.getDefault());
                        List<Address> addresses;
                        addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                        String state = addresses.get(0).getAdminArea();
                        // Make sure state is in database
                        if (adapter.getPosition(state) != -1) {
                            // Set state and TextViews to current location
                            int spinnerPos = adapter.getPosition(state);
                            spinner.setSelection(spinnerPos);
                            stateName.setText(state);
                            String rightscode = getRightsCodeFromDB(Integer.toString(spinnerPos));
                            rightType.setText(getRightsType(rightscode));
                            rights.setText(getRights(rightscode));
                        } else {
                            // Don't have rights for current location
                            Toast.makeText(YourRights.this, "Rights not found for current location", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Error getting location.");
                    }
                } else {
                    // Couldn't get location
                    Toast.makeText(YourRights.this, "Could not get location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Called when the activity enters view
     */
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * Called when the activity exits view
     */
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Gets state from settings
     * @return The state, or null if none is set
     */
    protected String getState(){
        // Get the state
        return prefs.getString("State", null);
    }

    /**
     * Get the rights from the database
     * @param state The state to retrieve
     * @return The rights code for the state, or "not found" if not found
     */
    protected String getRightsCodeFromDB(String state) {
        // Check if the state was actually retrieved
        if (state != null) {
            Cursor curs = MainActivity.db.rawQuery("select rights from Rights where state = " + state + ";", null);
            curs.moveToFirst();
            if (!curs.isAfterLast())
                // Return the rights
                return curs.getString(curs.getColumnIndex("rights"));
            curs.close();
        }
        return "not found";
    }

    /**
     * Takes the output from a query and matches the code with the type of rights associated with it
     * @param rightType The rights code from the state retrieved
     * @return The rights explanation for the rights code
     */
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

    /**
     * Takes the output from a query and matches the code with the full name of the rights associated with it
     * @param rightCode The rights code from the state retrieved
     * @return The full name of the rights code
     */
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

    /**
     * Gets the user's location
     * @return The Location object
     */
    private Location getLocation() {
        try {
            // Check permissions
            int permissionCheck = ContextCompat.checkSelfPermission(YourRights.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(YourRights.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CLOCATION);
            }
            permissionCheck = ContextCompat.checkSelfPermission(YourRights.this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(YourRights.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FLOCATION);
            }

            // Get the location manager
            LocationManager locManager = (LocationManager) this.getBaseContext().getSystemService(LOCATION_SERVICE);
            // Check for enabled services
            boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            // If the device cannot determine location
            if (!isGPSEnabled && !isNetworkEnabled) {
                return null;
            } else {
                // If the device is network connected
                if (isNetworkEnabled) {
                    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, (float) 50.0, this);
                    return locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                } else {
                    // The device is GPS connected
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
