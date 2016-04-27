package com.cs495.bystander;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Locale;
import android.speech.RecognizerIntent;
import android.content.ActivityNotFoundException;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    public static SQLiteDatabase db;
    int PERMISSION_CAMERA;
    int PERMISSION_STORAGE;
    int PERMISSION_AUDIO;
    String FILENAME;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int REQ_CODE_TITLE = 101;
    boolean manualVideoDescriptions;
    String TOKEN;
    String TITLE;
    String DESCRIPTION;
    boolean isPublic;
    boolean automaticUpload;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB mydb = new DB(this);
        db = mydb.makeDB();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        }
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
        }
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_AUDIO);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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
            default:
                return false;
        }
    }

    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    public void takeVideo(View view) {
        Uri fileUri;

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File file = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        if (file != null)
            FILENAME = file.toString();
        fileUri = Uri.fromFile(file); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                automaticUpload = prefs.getBoolean("example_switch", false);
                isPublic = prefs.getBoolean("broadcast", false);
                TOKEN = prefs.getString("oauth", null);

                if (automaticUpload) {
                    if (isDeviceOnline(this)) {
                        manualVideoDescriptions = prefs.getBoolean("use_speech", false);
                        if (manualVideoDescriptions) { // if you upload descriptions using speech to text
                            promptSpeechInput("description");
                            promptSpeechInput("title");
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Video Details");
                            LinearLayout layout = new LinearLayout(this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            final EditText titleInput = new EditText(this);
                            final EditText descInput = new EditText(this);
                            titleInput.setInputType(InputType.TYPE_CLASS_TEXT);
                            descInput.setInputType(InputType.TYPE_CLASS_TEXT);
                            titleInput.setHint("Title");
                            descInput.setHint("Description");
                            layout.addView(titleInput);
                            layout.addView(descInput);
                            builder.setView(layout);
                            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TITLE = titleInput.getText().toString();
                                    DESCRIPTION = descInput.getText().toString();
                                    new UploadVideo(FILENAME, TITLE, DESCRIPTION, true, isPublic, TOKEN);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(MainActivity.this, "Video Upload Cancelled", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                    } else {
                        Toast.makeText(this, "Device is not online. Please manually upload later.", Toast.LENGTH_SHORT).show();
                    }
                } else { // Ask for manual upload
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Would you like to upload this video?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            manualVideoDescriptions = prefs.getBoolean("use_speech", false);
                            if (manualVideoDescriptions) { // if you upload descriptions using speech to text
                                promptSpeechInput("description");
                                promptSpeechInput("title");
                            } else {
                                AlertDialog.Builder upload = new AlertDialog.Builder(MainActivity.this);
                                upload.setTitle("Video Details");
                                LinearLayout layout = new LinearLayout(MainActivity.this);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                final EditText titleInput = new EditText(MainActivity.this);
                                final EditText descInput = new EditText(MainActivity.this);
                                titleInput.setInputType(InputType.TYPE_CLASS_TEXT);
                                descInput.setInputType(InputType.TYPE_CLASS_TEXT);
                                titleInput.setHint("Title");
                                descInput.setHint("Description");
                                layout.addView(titleInput);
                                layout.addView(descInput);
                                upload.setView(layout);
                                upload.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        TITLE = titleInput.getText().toString();
                                        DESCRIPTION = descInput.getText().toString();
                                        new UploadVideo(FILENAME, TITLE, DESCRIPTION, true, isPublic, TOKEN);
                                    }
                                });
                                upload.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(MainActivity.this, "Video Upload Cancelled", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });
                                upload.show();
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }

            }
        } else if (REQ_CODE_SPEECH_INPUT == requestCode) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                DESCRIPTION = result.get(0);
                isPublic = prefs.getBoolean("broadcast", false);
                TOKEN = prefs.getString("oauth", null);
                new UploadVideo(FILENAME, TITLE, DESCRIPTION, manualVideoDescriptions, isPublic, TOKEN);
            }
        } else if (REQ_CODE_TITLE == requestCode) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                TITLE = result.get(0);
            }
        }
    }


    public boolean isDeviceOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }


    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Bystander");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Bystander", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
            MainActivity.db.execSQL("INSERT OR REPLACE INTO videos (filename) VALUES (\'" + mediaFile.toString() + "\')");
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
        try {
            if (part.equals("description")) {
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Add a title for your video");
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } else if (part.equals("title")) {
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Add a description to your video");
                startActivityForResult(intent, REQ_CODE_TITLE);
            }
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Speech not supported!",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
