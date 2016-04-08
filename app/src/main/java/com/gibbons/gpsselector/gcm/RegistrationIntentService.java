package com.gibbons.gpsselector.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.gibbons.gpsselector.util.Constants;
import com.gibbons.gpsselector.util.QuickstartPreferences;
import com.gibbons.gpsselector.R;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }


    protected void onHandleIntents(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            //sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        // ...
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String regid = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Context context = getApplicationContext();

            subscribeTopics(regid);
            SharedPreferences.Editor editor = getSharedPreferences("Accounts", Context.MODE_PRIVATE).edit();

            editor.putString("regid", regid);
            editor.apply();
            SharedPreferences prefs = context.getSharedPreferences("Accounts",Context.MODE_PRIVATE);
            String uid = prefs.getString("uid", "");
            String url = Constants.SERVER_URL + "/api/users/add/" + uid + "/" + regid;
            HttpClient client = HttpClientBuilder.create().build();
            HttpPut httpPut = new HttpPut(url);
            try {
                HttpResponse response = client.execute(httpPut);

                while(response.getStatusLine().getStatusCode()!=200) {
                    wait(1000);
                    response = client.execute(httpPut);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
        // ...


    // ...
}
