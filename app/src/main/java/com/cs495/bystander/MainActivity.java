package com.cs495.bystander;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Locale;
import android.speech.RecognizerIntent;
import android.content.ActivityNotFoundException;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    public static SQLiteDatabase db;
    int PERMISSION_CAMERA;
    int PERMISSION_STORAGE;
    String FILENAME;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int REQ_CODE_TITLE = 101;
    boolean manualVideoDescriptions = true;
    String TITLE;
    String DESCRIPTION;
    int partofdescription;
    boolean isPublic = true;
    boolean automaticUpload = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB mydb = new DB(this);
        db = mydb.makeDB();


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);

    }

    // method to init return a database
    public static SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // method to start the settings activity when action bar drop down settings item is clicked
    // taken from an Android dev tutorial
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            case R.id.yourRights:
                intent = new Intent(this, YourRights.class);
                startActivity(intent);
            case R.id.badsettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            default:
                return false;
        }
    }

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    public void takeVideo(View view) {

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File file = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        FILENAME = file.toString();
        fileUri = Uri.fromFile(file); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        System.out.println("filename " + fileUri.toString());

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                if (automaticUpload) {
                    if (isDeviceOnline(this)) {
                        if (manualVideoDescriptions) { // if you upload descriptions using speech to text
                            promptSpeechInput("description");
                            promptSpeechInput("title");
                        } else {
                            new UploadVideo(FILENAME);
                        }
                    } else {
                        Toast.makeText(this, "Device is not online. Please manually upload later.", Toast.LENGTH_SHORT).show();
                    }
                }
                // Do something with the contact here (bigger example below)
            }
        } else if (REQ_CODE_SPEECH_INPUT == requestCode) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                DESCRIPTION = result.get(0);
                System.out.println("GOT TEXT!!!! " + result.get(0));
                System.out.println("DESCRIPTION " + DESCRIPTION);
                new UploadVideo(FILENAME, TITLE, DESCRIPTION, manualVideoDescriptions, isPublic);
            }
        } else if (REQ_CODE_TITLE == requestCode) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                TITLE = result.get(0);
                System.out.println("GOT TEXT!!!! " + result.get(0));
                System.out.println("TITLE " + TITLE);
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


    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
            System.out.println("MEDIA FILE " + mediaFile);
            MainActivity.db.execSQL("INSERT OR REPLACE INTO videos (filename) VALUES (\'" + mediaFile.toString() + "\')");
        } else if (type == MEDIA_TYPE_VIDEO) {
/*            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");*/
            mediaFile = new File(mediaStorageDir.getPath() +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    /**
     * Showing google speech input dialog
     * */
    public void promptSpeechInput(String part) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "say what's on your mind");
        try {
            if (part == "description") {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } else if (part == "title"){
                startActivityForResult(intent, REQ_CODE_TITLE);
            }
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Speech not supported!",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
