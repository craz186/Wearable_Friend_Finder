package com.gibbons.gpsselector.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.gibbons.gpsselector.fragments.FriendRequestDialogFragment;
import com.gibbons.gpsselector.fragments.LoginDialogFragment;
import com.gibbons.gpsselector.util.Constants;
import com.gibbons.gpsselector.R;
import com.gibbons.gpsselector.gps.MyLocation;


import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class NotificationActivity extends AppCompatActivity {

    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        final String fromuid = extras.getString("fromuid");
        final String request = extras.getString("request");
        setContentView(R.layout.activity_gps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mLocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
//        SingleShotLocationProvider.requestSingleUpdate(getApplicationContext(),
//                new SingleShotLocationProvider.LocationCallback() {
//                    @Override
//                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
//                        Log.d("Location", "my location is " + location.toString());
//                        sendToServer(fromuid, location.latitude, location.longitude);
//                    }
//                });
        if(request.equals("FriendRequest")) {

            DialogFragment newFragment = new FriendRequestDialogFragment();
            Bundle args = new Bundle();
            args.putString("fromuid", fromuid);
            newFragment.setArguments(args);
            newFragment.show(getFragmentManager(), "missiles");
        }
        else {
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    //Got the location!
                    sendToServer(fromuid, location.getLatitude(), location.getLongitude());
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);
        }
    }

    private void sendToServer(String fromuid, double latitude, double longitude) {
        new NotifyUser(fromuid, latitude, longitude).execute();
    }

    private Location getLastBestLocation() {

        Location locationGPS = null;
        Location locationNet = null;
        try {
            locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }

    public class NotifyUser extends AsyncTask<Void, Void, Boolean> {

        private String fromuid;
        private double latitude;
        private double longitude;

        NotifyUser(String uid, double latitude, double longitude) {
            this.fromuid = uid;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            HttpClient client = HttpClientBuilder.create().build();
            String url = Constants.SERVER_URL + "/api/users/notify/" + fromuid + "/" + latitude + "/" + longitude + "/";

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
                finish();
            }
        }

        @Override
        protected void onCancelled() {
        }

    }
}
