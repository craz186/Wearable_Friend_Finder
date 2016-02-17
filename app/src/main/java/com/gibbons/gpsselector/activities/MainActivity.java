package com.gibbons.gpsselector.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.gibbons.gpsselector.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button map = (Button) findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startMaps(v);
            }
        });
        Button friends = (Button) findViewById(R.id.friends);
        friends.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startFriends(v);
            }
        });
        SharedPreferences preferences = getSharedPreferences("Accounts", Context.MODE_PRIVATE);
        String uid = preferences.getString("account","");
        if(uid.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void startMaps(View view) {
        Intent intent = new Intent(this, MainMapsActivity.class);
        startActivity(intent);
    }

    public void startFriends(View view) {
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }
}
