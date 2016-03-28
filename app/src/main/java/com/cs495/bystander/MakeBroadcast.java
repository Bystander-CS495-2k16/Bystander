package com.cs495.bystander;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastSnippet;
import com.google.api.services.youtube.model.LiveBroadcastStatus;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import com.google.api.services.youtube.model.LiveStreamSnippet;
import com.google.api.services.youtube.model.CdnSettings;
import com.google.api.services.youtube.model.LiveStream;

import android.app.Activity;
import android.view.View;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by appleowner on 3/10/16.
 */
public class MakeBroadcast {
    String TOKEN;
    Activity mainAct;
    public LiveStream liveStream;
    public String RTMP_ADDRESS;
    public String RTMP_STREAM_NAME;

    public MakeBroadcast(String token, Activity activity) {
        System.out.println("sksdjkfjjfjf");
        TOKEN = token;
        System.out.println("toke: " + token);
        mainAct = activity;
        new makeBroadcast(token).execute();
    }

    public void make() {

    }

    public class makeBroadcast extends AsyncTask<Void, Void, Void> {
        Context mActivity; // changed this to context from example code's activity type
        String mScope;
        String mUsername;

        makeBroadcast(String token) {
            System.out.println("heyyyyy");
            this.mUsername = token;
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected Void doInBackground(Void... params) {
            //System.out.println("DLKJSLKJFLKJFDLKJSDLKJSFLKJ");
            makeYouTube();
            return null;
        }

        public void makeYouTube() {
            System.out.println("HEODLK");
            HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            JacksonFactory JSON_FACTORY = new JacksonFactory();
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(HTTP_TRANSPORT).build();
            System.out.println("as;kjf;askdjf;sakjf;laskj;sadlkjf");

            credential.setAccessToken(TOKEN);
            System.out.println("sdafja;ksfjd;askj");

            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("com.cs495.bystander").build();
            System.out.println("boooooop");

            String title = "new video";
            LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
            broadcastSnippet.setTitle(title);


            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
            Calendar cal = Calendar.getInstance();
            System.out.println(dateFormat.format(cal.getTime())); //2014/08/06 16:00:22
            broadcastSnippet.setScheduledStartTime(new DateTime(cal.getTime()));

            //broadcastSnippet.setScheduledEndTime(new DateTime("2024-01-31T00:00:00.000Z"));
            System.out.println("after snippet");
            LiveBroadcastStatus status = new LiveBroadcastStatus();
            status.setPrivacyStatus("private");
            System.out.println("after status ");
            LiveBroadcast broadcast = new LiveBroadcast();
            System.out.println("after broadcast");
            broadcast.setKind("youtube#liveBroadcast");
            broadcast.setSnippet(broadcastSnippet);
            broadcast.setStatus(status);

            // Construct and execute the API request to insert the broadcast.
            try {
                YouTube.LiveBroadcasts.Insert liveBroadcastInsert =
                        youtube.liveBroadcasts().insert("snippet,status", broadcast);
                System.out.println("trying to insert");
                LiveBroadcast returnedBroadcast = liveBroadcastInsert.execute();
                // Print information from the API response.
                System.out.println("\n================== Returned Broadcast ==================\n");
                System.out.println("  - Id: " + returnedBroadcast.getId());
                System.out.println("  - Title: " + returnedBroadcast.getSnippet().getTitle());
                System.out.println("  - Description: " + returnedBroadcast.getSnippet().getDescription());
                System.out.println("  - Published At: " + returnedBroadcast.getSnippet().getPublishedAt());
                System.out.println(
                        "  - Scheduled Start Time: " + returnedBroadcast.getSnippet().getScheduledStartTime());
                System.out.println(
                        "  - Scheduled End Time: " + returnedBroadcast.getSnippet().getScheduledEndTime());

                LiveStreamSnippet streamSnippet = new LiveStreamSnippet();
                streamSnippet.setTitle(title);
                CdnSettings cdnSettings = new CdnSettings();
                cdnSettings.setFormat("1080p");
                cdnSettings.setIngestionType("rtmp");


                LiveStream stream = new LiveStream();
                liveStream = stream;
                stream.setKind("youtube#liveStream");
                stream.setSnippet(streamSnippet);
                stream.setCdn(cdnSettings);



                // Construct and execute the API request to insert the stream.
                YouTube.LiveStreams.Insert liveStreamInsert =
                        youtube.liveStreams().insert("snippet,cdn", stream);
                LiveStream returnedStream = liveStreamInsert.execute();
                System.out.println("address " + returnedStream.getCdn().getIngestionInfo().getIngestionAddress());
                RTMP_ADDRESS = returnedStream.getCdn().getIngestionInfo().getIngestionAddress();
                System.out.println("rtmp address " + returnedStream.getCdn().getIngestionInfo().getIngestionAddress());
                System.out.println("rtmp stream name " + returnedStream.getCdn().getIngestionInfo().getStreamName());
                RTMP_STREAM_NAME = returnedStream.getCdn().getIngestionInfo().getStreamName();


                // Print information from the API response.
                System.out.println("\n================== Returned Stream ==================\n");
                System.out.println("  - Id: " + returnedStream.getId());
                System.out.println("  - Title: " + returnedStream.getSnippet().getTitle());
                System.out.println("  - Description: " + returnedStream.getSnippet().getDescription());
                System.out.println("  - Published At: " + returnedStream.getSnippet().getPublishedAt());

                // Construct and execute a request to bind the new broadcast
                // and stream.
                YouTube.LiveBroadcasts.Bind liveBroadcastBind =
                        youtube.liveBroadcasts().bind(returnedBroadcast.getId(), "id,contentDetails");
                liveBroadcastBind.setStreamId(returnedStream.getId());
                returnedBroadcast = liveBroadcastBind.execute();


                // Print information from the API response.
                System.out.println("\n================== Returned Bound Broadcast ==================\n");
                System.out.println("  - Broadcast Id: " + returnedBroadcast.getId());
                System.out.println(
                        "  - Bound Stream Id: " + returnedBroadcast.getContentDetails().getBoundStreamId());




            } catch (Exception e) {
                handleException(e);
            }

        }


        public void handleException(final Exception e) {
            // Because this call comes from the AsyncTask, we must ensure that the following
            // code instead executes on the UI thread.
            mainAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (e instanceof Exception) {
                        // The Google Play services APK is old, disabled, or not present.
                        // Show a dialog created by Google Play services that allows
                        // the user to update the APK
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * This method is a hook for background threads and async tasks
         *that need to
         * provide the user a response UI when an exception occurs.
         */

    }
}