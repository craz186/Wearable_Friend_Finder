package com.gibbons.gpsselector.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.gibbons.gpsselector.Constants;
import com.gibbons.gpsselector.R;
import com.gibbons.gpsselector.gps.SingleShotLocationProvider;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class GPSActivity extends AppCompatActivity {

    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        final String fromuid = extras.getString("fromuid");
        setContentView(R.layout.activity_gps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mLocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        SingleShotLocationProvider.requestSingleUpdate(getApplicationContext(),
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.d("Location", "my location is " + location.toString());
                        sendToServer(fromuid, location.latitude, location.longitude);
                    }
                });

    }

    private void sendToServer(String fromuid, float latitude, float longitude) {
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
        private float latitude;
        private float longitude;

        NotifyUser(String uid, float latitude, float longitude) {
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
