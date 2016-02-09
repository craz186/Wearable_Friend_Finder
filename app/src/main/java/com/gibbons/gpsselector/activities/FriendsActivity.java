package com.gibbons.gpsselector.activities;

import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.gibbons.gpsselector.Constants;
import com.gibbons.gpsselector.R;
import com.gibbons.gpsselector.fragments.FriendsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class FriendsActivity extends ListActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                String url = Constants.serverUrl+"/api/user/friends/9b938710-2111-38f2-902f-9ed4357cd05c/";

                HttpGet request = new HttpGet(url);
                HttpResponse response = null;
                try {
                    response = client.execute(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
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
                        names.add(object.getString("name"));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1,names);
                    getListView().setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //TODO convert returned document to Object
            }
        };

        task.execute();
        //TODO Set click listener for list if clicked. Will send http to server and retrieve gps coords for user.
    }

}
