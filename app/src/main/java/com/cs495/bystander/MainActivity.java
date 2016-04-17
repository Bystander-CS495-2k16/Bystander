package com.cs495.bystander;

import android.Manifest;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity  {

    public static SQLiteDatabase db;
    int PERMISSION_CAMERA;
    int PERMISSION_STORAGE;

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

    public void goToRights(View view) {
        Intent intent = new Intent(this, YourRights.class);
        startActivity(intent);

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
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
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

        File fileUri = getOutputMediaFile(MEDIA_TYPE_VIDEO); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);


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
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }






}
