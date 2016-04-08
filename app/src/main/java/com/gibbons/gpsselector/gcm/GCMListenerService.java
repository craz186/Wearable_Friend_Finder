package com.gibbons.gpsselector.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gibbons.gpsselector.activities.GPSActivity2;
import com.gibbons.gpsselector.util.Constants;
import com.gibbons.gpsselector.R;
import com.gibbons.gpsselector.activities.FriendRequestActivity;
import com.gibbons.gpsselector.activities.NotificationActivity;

import com.gibbons.gpsselector.gps.MyLocation;
import com.google.android.gms.gcm.GcmListenerService;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class GCMListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String uid = data.getString("uid");
        String latitude = data.getString("latitude");
        String longitude = data.getString("longitude");
        String request = data.getString("request");
        Log.d("DEBUG", "From: " + from);
        Log.d("DEBUG", "Message: " + uid);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        }

        if(request.equals("GPSInfo")){
            calculatePath(latitude,longitude);
        }
        else {
            //ask user if they want to friends.
            sendNotification(uid, request);
        }
    }

    public void friendRequest(String uid) {
//        Intent intent = new Intent(this,FriendRequestActivity.class);
//        intent.setFlag(Intent.FLAG_ACTIVITY_NEW_TASK);
////UNIQUE_ID if you expect more than one notification to appear
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this, FriendRequestActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification noti = new Notification.Builder(this)
                .setContentTitle(getText(R.string.app_name))
                .setContentText("Friend Request")
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
//        Intent intent = new Intent(this, GPSActivity2.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0/* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_stat_ic_notification)
//                .setContentTitle("Friend Request")
//                .setContentText(uid)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
    
    private void calculatePath(final String latitude, final String longitude) {


        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
                new CalculatePath(location.getLatitude()+","+location.getLongitude(),latitude+","+longitude).execute();
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
    }

    private void sendNotification(String message, String requestType) {

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("fromuid",message); //TODO add actual uid
        intent.putExtra("request",requestType);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(requestType)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public class CalculatePath extends AsyncTask<Void, Void, Boolean> {

        private String startGps;
        private String endGps;

        CalculatePath(String startGps, String endGps) {
            this.startGps = startGps;
            this.endGps = endGps;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            HttpClient client = HttpClientBuilder.create().build();
            String url = Constants.SERVER_URL + "/api/path/calculate/" + startGps + "/" + endGps + "/";

            HttpPost request = new HttpPost(url);
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
