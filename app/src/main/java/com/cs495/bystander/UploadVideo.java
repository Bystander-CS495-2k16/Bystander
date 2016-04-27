/**************
* Brian Burns
* Amy Puente
* Amy Chockley
* Bystander
* Uploads a video to YouTube
* UploadVideo.java
**************/

package com.cs495.bystander;

/*
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

import android.os.AsyncTask;
import android.util.Log;

/**
 * Uploads a video to YouTube
 */
public class UploadVideo {
    // Video fields
    private String TOKEN;
    private String filename;
    private String TITLE;
    private String DESCRIPTION;
    private boolean MANUALDESCRIPTIONS;
    private boolean isPublic;

    /**
     * Constructor, gets video details
     * @param filename The name of the captured video
     * @param title The title of the video
     * @param description The description of the video
     * @param manualDescriptions Whether a description was given
     * @param isPublic 'true' if video is to be public, 'false' if not
     * @param token The user's OAuth 2.0 token
     */
    public UploadVideo(String filename, String title, String description, boolean manualDescriptions, boolean isPublic, String token) {
        // Set state variables
        this.filename = filename;
        this.TITLE = title;
        this.DESCRIPTION = description;
        this.MANUALDESCRIPTIONS = manualDescriptions;
        this.isPublic = isPublic;
        TOKEN = token;
        // Log the upload
        Log.d("UPLOADVIDEO", "FILENAME " + filename + " title " + TITLE + " description " + DESCRIPTION);
        // Start the upload
        UploadTask up = new UploadTask();
        up.execute(filename);
    }

    /**
     * Upload helper to execute the video upload
     */
    private class UploadTask extends AsyncTask<String, Integer, Integer> {
      /**
       * Runs the upload task in the background
       */
        protected Integer doInBackground(String ... file) {
            // Initialize YouTube and upload the video
            upload(initYouTube(), filename);
            return 1;
        }

        /**
         * Called when the progress of the upload updates
         */
        protected void onProgressUpdate(Integer... progress) {

        }

        /**
         * Initializes YouTube
         * @return The YouTube API
         */
        public YouTube initYouTube() {
            // Connect to the YouTube API
            HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            JacksonFactory JSON_FACTORY = new JacksonFactory();
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(HTTP_TRANSPORT).build();
            credential.setAccessToken(TOKEN);
            return new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("com.cs495.bystander").build();
        }

        /**
         * Upload a video to YouTube
         * @param youtube The YouTube API
         * @param filename The name of the captured video
         */
        public void upload(YouTube youtube, String filename) {
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
                List<String> tags = new ArrayList<>();
                tags.add("Bystander");
                snippet.setTags(tags);

                // Add the completed snippet object to the video resource.
                videoObjectDefiningMetadata.setSnippet(snippet);

                InputStream in = new BufferedInputStream(new FileInputStream(new File(filename)));
                InputStreamContent mediaContent = new InputStreamContent("video/*", in);

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
                                Log.d("UPLOADVIDEO", "Initiation Started");
                                break;
                            case INITIATION_COMPLETE:
                                Log.d("UPLOADVIDEO", "Initiation Completed");
                                break;
                            case MEDIA_IN_PROGRESS:
                                Log.d("UPLOADVIDEO", "Upload in progress");
                                Log.d("UPLOADVIDEO", "Upload percentage: " + uploader.getNumBytesUploaded());
                                break;
                            case MEDIA_COMPLETE:
                                Log.d("UPLOADVIDEO", "Upload Completed!");
                                break;
                            case NOT_STARTED:
                                Log.d("UPLOADVIDEO", "Upload Not Started!");
                                break;
                        }
                    }
                };
                uploader.setProgressListener(progressListener);

                // Call the API and upload the video.
                Video returnedVideo = videoInsert.execute();

                // Print data about the newly inserted video from the API response.
                Log.d("UPLOADVIDEO", "\n================== Returned Video ==================\n");
                Log.d("UPLOADVIDEO", "  - Id: " + returnedVideo.getId());
                Log.d("UPLOADVIDEO", "  - Title: " + returnedVideo.getSnippet().getTitle());
                Log.d("UPLOADVIDEO", "  - Tags: " + returnedVideo.getSnippet().getTags());
                Log.d("UPLOADVIDEO", "  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
                Log.d("UPLOADVIDEO", "  - Video Count: " + returnedVideo.getStatistics().getViewCount());

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
