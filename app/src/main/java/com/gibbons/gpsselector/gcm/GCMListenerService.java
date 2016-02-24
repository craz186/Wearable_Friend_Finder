package com.gibbons.gpsselector.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gibbons.gpsselector.Constants;
import com.gibbons.gpsselector.R;
import com.gibbons.gpsselector.activities.GPSActivity;
import com.gibbons.gpsselector.activities.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class GCMListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("uid");
        String latitude = data.getString("latitude");
        String longitude = data.getString("longitude");
        Log.d("DEBUG", "From: " + from);
        Log.d("DEBUG", "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        if(latitude == null || longitude == null) {
            sendNotification(message);
        }
        else {
            calculatePath(latitude,longitude);
        }
    }

    private void calculatePath(String latitude, String longitude) {

    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, GPSActivity.class);
        intent.putExtra("fromuid",message); //TODO add actual uid
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public class CalculatePath extends AsyncTask<Void, Void, Boolean> {

        private String fromuid;
        private float latitude;
        private float longitude;

        CalculatePath(String uid, float latitude, float longitude) {
            this.fromuid = uid;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            HttpClient client = HttpClientBuilder.create().build();
            String url = Constants.SERVER_URL + "/api/path/calculate/" + fromuid + "/" + latitude + "/" + longitude + "/";

            HttpGet request = new HttpGet(url);
            HttpResponse response;
            try {
                response = client.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                // Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
        }

    }
}
