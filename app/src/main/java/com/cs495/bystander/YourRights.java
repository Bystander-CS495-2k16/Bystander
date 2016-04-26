package com.cs495.bystander;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;

public class YourRights extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_rights);

        TextView rights = (TextView)findViewById(R.id.rightsextView);
        // set the text to be the user's rights, by querying the db using the state preference
        rights.setText(getRights(getRightsCodeFromDB(getState())));
    }

    // gets State from settings, returns null if not found
    protected String getState(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
                return "This is a 1- party consent state. This means one party must consent to the recording.";
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

}
