package com.cs495.bystander;

/**
 * Created by appleowner on 4/16/16.
 * Based heavily off the Google Developer Tutorial here https://developers.google.com/youtube/v3/code_samples/java#upload_a_video
 */

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;


public class UploadVideo {
    private String TOKEN;
    private String filename;
    private String TITLE;
    private String DESCRIPTION;
    private boolean MANUALDESCRIPTIONS;
    private boolean isPublic;
    SharedPreferences prefs;

    public UploadVideo(String fileName) {
        this.filename = fileName;
        System.out.println("FILENAME " + fileName);
        //upload(initYouTube(), fileName);
        UploadTask up = new UploadTask();
        up.execute(fileName);
        // Todo: need to deal with null token in Main Activity
    }

    public UploadVideo(String filename, String title, String description, boolean manualDescriptions, boolean isPublic, String token) {
        this.filename = filename;
        this.TITLE = title;
        this.DESCRIPTION = description;
        this.MANUALDESCRIPTIONS = manualDescriptions;
        this.isPublic = isPublic;
        TOKEN = token;
        System.out.println("TOKEN: " + TOKEN);
        System.out.println("FILENAME " + filename + " title " + TITLE + " description " + DESCRIPTION);
        UploadTask up = new UploadTask();
        up.execute(filename);
    }

//    public void getToken() {
//        Cursor c = MainActivity.db.rawQuery("SELECT * FROM tokens", null);
//        c.moveToFirst();
//        c.moveToNext(); // skip android_metadata table"insert
//        int i = 0;
//        while (c.isAfterLast() == false) {
//            System.out.println(c.toString());
//            TOKEN = c.getString( c.getColumnIndex("token"));
//            System.out.println("token: " + c.getString( c.getColumnIndex("token")));
//            System.out.println(c.getString(c.getColumnIndex("email")));
//            c.moveToNext();
//            i++;
//        }
//        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//    }

    private class UploadTask extends AsyncTask<String, Integer, Integer> {
        protected Integer doInBackground(String ... file) {
            upload(initYouTube(), filename);
            return 1;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }

        public YouTube initYouTube() {
            HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            JacksonFactory JSON_FACTORY = new JacksonFactory();
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(HTTP_TRANSPORT).build();
            credential.setAccessToken(TOKEN);
            return new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("com.cs495.bystander").build();
        }

        public void upload(YouTube youtube, String filename) {
            System.out.println("Uploading: " + filename);
            try {

                // Add extra information to the video before uploading.
                Video videoObjectDefiningMetadata = new Video();

                // Set the video to be publicly visible. This is the default
                // setting. Other supporting settings are "unlisted" and "private."
                VideoStatus status = new VideoStatus();
                if (isPublic) {
                    status.setPrivacyStatus("public");
                } else {
                    status.setPrivacyStatus("private");
                }
                videoObjectDefiningMetadata.setStatus(status);

                VideoSnippet snippet = new VideoSnippet();

                // This code uses a Calendar instance to create a unique name and
                // description for test purposes so that you can easily upload
                // multiple files. You should remove this code from your project
                // and use your own standard names instead.
                Calendar cal = Calendar.getInstance();
                if (MANUALDESCRIPTIONS) {
                    snippet.setTitle(TITLE);
                    snippet.setDescription(DESCRIPTION);
                } else {
                    snippet.setTitle("Test Upload via Java on " + cal.getTime());
                    snippet.setDescription(
                            "Video uploaded via YouTube Data API V3 using the Java library " + "on " + cal.getTime());
                }

                // Set the keyword tags that you want to associate with the video.
                List<String> tags = new ArrayList<String>();
                tags.add("Bystander");
                //tags.add("example");
                //tags.add("java");
                //tags.add("YouTube Data API V3");
                //tags.add("erase me");
                snippet.setTags(tags);

                // Add the completed snippet object to the video resource.
                videoObjectDefiningMetadata.setSnippet(snippet);

                //InputStreamContent mediaContent = new InputStreamContent("video/*",
                //        UploadVideo.class.getResourceAsStream(filename));

                InputStream in = new BufferedInputStream(new FileInputStream(new File(filename)));
                File f = new File(filename);
                System.out.println("instream length " + f.length());
                InputStreamContent mediaContent = new InputStreamContent("video/*", in);
                System.out.println("LENGTH OF STREAMZZZ " + mediaContent.getLength());
                /*while (mediaContent.getLength() == -1) {
                    try {
                        System.out.println("waiting...");
                        Thread.sleep(2000);                 //1000 milliseconds is one second.
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }*/

                // Insert the video. The command sends three arguments. The first
                // specifies which information the API request is setting and which
                // information the API response should return. The second argument
                // is the video resource that contains metadata about the new video.
                // The third argument is the actual video content.
                YouTube.Videos.Insert videoInsert = youtube.videos()
                        .insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

                // Set the upload type and add an event listener.
                MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

                // Indicate whether direct media upload is enabled. A value of
                // "True" indicates that direct media upload is enabled and that
                // the entire media content will be uploaded in a single request.
                // A value of "False," which is the default, indicates that the
                // request will use the resumable media upload protocol, which
                // supports the ability to resume an upload operation after a
                // network interruption or other transmission failure, saving
                // time and bandwidth in the event of network failures.
                uploader.setDirectUploadEnabled(false);

                MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                    public void progressChanged(MediaHttpUploader uploader) throws IOException {
                        switch (uploader.getUploadState()) {
                            case INITIATION_STARTED:
                                System.out.println("Initiation Started");
                                break;
                            case INITIATION_COMPLETE:
                                System.out.println("Initiation Completed");
                                break;
                            case MEDIA_IN_PROGRESS:
                                System.out.println("Upload in progress");
                                System.out.println("Upload percentage: " + uploader.getNumBytesUploaded());
                                break;
                            case MEDIA_COMPLETE:
                                System.out.println("Upload Completed!");
                                break;
                            case NOT_STARTED:
                                System.out.println("Upload Not Started!");
                                break;
                        }
                    }
                };
                uploader.setProgressListener(progressListener);

                // Call the API and upload the video.
                Video returnedVideo = videoInsert.execute();

                // Print data about the newly inserted video from the API response.
                System.out.println("\n================== Returned Video ==================\n");
                System.out.println("  - Id: " + returnedVideo.getId());
                System.out.println("  - Title: " + returnedVideo.getSnippet().getTitle());
                System.out.println("  - Tags: " + returnedVideo.getSnippet().getTags());
                System.out.println("  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
                System.out.println("  - Video Count: " + returnedVideo.getStatistics().getViewCount());

            } catch (GoogleJsonResponseException e) {
                System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("IOException: " + e.getMessage());
                e.printStackTrace();
            } catch (Throwable t) {
                System.err.println("Throwable: " + t.getMessage());
                t.printStackTrace();
            }

        }

    }

}
