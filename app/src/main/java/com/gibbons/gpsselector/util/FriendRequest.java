package com.gibbons.gpsselector.util;

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gibbons.gpsselector.fragments.LoginDialogFragment;
import com.gibbons.gpsselector.util.Constants;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

import static com.gibbons.gpsselector.fragments.LoginDialogFragment.*;

/**
 * Created by User on 3/16/2016.
 */
public class FriendRequest{

    public static void addFriend(String mUsername, String mUid) {
        AddFriendTask friendTask = new AddFriendTask(mUsername, mUid);
        friendTask.execute();

    }

    public static class AddFriendTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mUid;

        AddFriendTask(String username, String uid) {
            mUsername = username;
            mUid = uid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //TODO THIS LINE BREAKS
            InputStream entity;
            HttpClient client = HttpClientBuilder.create().build();
            String url = Constants.SERVER_URL + "/api/users/addFriend/" +mUid+ "/" + mUsername + "/";

            HttpPost request = new HttpPost(url);
            HttpResponse response = null;
            int status = 0;
            String uid = "";
            try {
                response = client.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
