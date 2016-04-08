package com.gibbons.gpsselector.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gibbons.gpsselector.R;
import com.gibbons.gpsselector.util.Constants;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

/**
 * Created by User on 4/6/2016.
 */
public class FriendRequestDialogFragment extends DialogFragment {

    Context mContext;
    Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mContext = activity.getApplicationContext();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle extras = mActivity.getIntent().getExtras();
        final String fromuid = extras.getString("fromuid");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        SharedPreferences prefs;
        String uid = "";
        if (mContext != null) {
            prefs = mContext.getSharedPreferences("Accounts", Context.MODE_PRIVATE);
            uid = prefs.getString("uid", "");
        }

        View inflateView = inflater.inflate(R.layout.dialog_friend_request, null);
        final TextView edit = (TextView)inflateView.findViewById(R.id.friendRequest);
        edit.setText("Friend Request from : " + fromuid);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final String finalUid = uid;
        builder.setView(inflateView)
                // Add action buttons
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        new AddFriendTask(fromuid,finalUid).execute();
                        //new AddFriendTask(text.trim(), finalUid).execute();
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public class AddFriendTask extends AsyncTask<Void, Void, Boolean> {

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
            mActivity.finish();
            return null;
        }

    }
}