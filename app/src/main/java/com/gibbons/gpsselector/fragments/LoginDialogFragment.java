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

import com.gibbons.gpsselector.util.Constants;
import com.gibbons.gpsselector.R;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class LoginDialogFragment extends DialogFragment {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        SharedPreferences prefs;
        String uid = "";
        if (mContext != null) {
            prefs = mContext.getSharedPreferences("Accounts", Context.MODE_PRIVATE);
            uid = prefs.getString("uid", "");
        }

        View inflateView = inflater.inflate(R.layout.dialog_signin, null);
        final EditText edit = (EditText)inflateView.findViewById(R.id.friendName);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final String finalUid = uid;
        builder.setView(inflateView)
                // Add action buttons
                .setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        String text=edit.getText().toString();
                        new NotifyFriend(text.trim(),finalUid).execute();
                        //new AddFriendTask(text.trim(), finalUid).execute();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
    public class NotifyFriend extends AsyncTask<Void, Void, Boolean> {

        private String toUser;
        private String fromuid;

        NotifyFriend(String toUser, String fromuid) {
            this.toUser = toUser;
            this.fromuid = fromuid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            HttpClient client = HttpClientBuilder.create().build();
            String url = Constants.SERVER_URL + "/api/users/notifyFriend/" + fromuid + "/" + toUser;

            HttpPost request = new HttpPost(url);
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
        protected void onCancelled() {
        }



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