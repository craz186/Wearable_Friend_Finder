package com.gibbons.gpsselector.gps;

/**
 * Created by User on 3/28/2016.
 */

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.gibbons.gpsselector.R;
import com.gibbons.gpsselector.gps.MyLocation;
import com.gibbons.gpsselector.util.Constants;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class GPSGetter {

    Context mContext;
    Location mLocation = null;

    public GPSGetter(Context context) {
        mContext = context;
    }

    public void findLocation() {

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
                mLocation = location;
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(mContext, locationResult);
    }

    public Location getLocation() {
        return mLocation;
    }

    private void sendToServer(String fromuid, double latitude, double longitude) {
        new NotifyUser(fromuid, latitude, longitude).execute();
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

    }
}
