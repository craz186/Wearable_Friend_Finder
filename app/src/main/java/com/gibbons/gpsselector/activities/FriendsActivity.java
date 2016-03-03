package com.gibbons.gpsselector.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.gibbons.gpsselector.Constants;
import com.gibbons.gpsselector.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class FriendsActivity extends ListActivity{


    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = getSharedPreferences("Accounts", Context.MODE_PRIVATE).getString("uid","");
        setContentView(R.layout.activity_friends);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            String s = "";

            @Override
            protected void onPreExecute() {
                //Show UI

            }

            @Override
            protected Void doInBackground(Void... arg0) {
                // do your background process
                InputStream entity;
                HttpClient client = HttpClientBuilder.create().build();

                String url = Constants.SERVER_URL +"/api/users/friends/"+uid+"/";

                HttpGet request = new HttpGet(url);
                HttpResponse response = null;
                try {
                    response = client.execute(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    assert response != null;
                    entity = response.getEntity().getContent();
                    int i;
                    s = "";
                    while((i =entity.read())!= -1) {
                        s += (char)i;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                //Show UI (Toast msg here)
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {
                    JSONArray json = new JSONArray(s);
                    JSONObject object = null;
                    LinkedList<String> names = new LinkedList<>();
                    for(int i =0; i<json.length(); i++) {
                        object = (JSONObject)json.get(i);
                        names.add(object.getString("name")+ " "+object.getString("uid"));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1,names);
                    getListView().setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //TODO convert returned document to Object
                //TODO check that the user has friends.
            }
        };

        task.execute();
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String touid = "" + parent.getItemAtPosition(position).toString().split(" ")[1];
                NotifyUser notify = new NotifyUser(touid, uid);
                notify.execute();
            }
        });
        //TODO Set click listener for list if clicked. Will send http to server and retrieve gps coords for user.
    }

    public class NotifyUser extends AsyncTask<Void, Void, Boolean> {

        private String touid;
        private String fromuid;

        NotifyUser(String touid, String fromuid) {
            this.touid = touid;
            this.fromuid = fromuid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            HttpClient client = HttpClientBuilder.create().build();
            String url = Constants.SERVER_URL + "/api/users/notify/" + fromuid + "/" + touid;

            HttpGet request = new HttpGet(url);
            HttpResponse response = null;
            int status = 0;
            String uid = "";
            try {
                response = client.execute(request);
                status = response.getStatusLine().getStatusCode();
                if (status == 409) {
                    //display incorrect password/user already exists
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                finish();
            }
        }

        @Override
        protected void onCancelled() {
        }



    }

}
