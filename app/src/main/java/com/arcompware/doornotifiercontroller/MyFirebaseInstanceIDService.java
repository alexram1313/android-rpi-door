package com.arcompware.doornotifiercontroller;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.UnsupportedEncodingException;

import static android.content.ContentValues.TAG;

/**
 * Created by alexram1313 on 8/8/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        //TODO: send to service

        String url = String.format(
                "http://%s:%s/regtoken/%s",
                getResources().getString(R.string.address),
                getResources().getString(R.string.port),
                refreshedToken
        );

        VolleyMgr.sendGetRequest(this, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Do nothing
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Log.d("derp", "Can't connect");
                        }else {
                            Log.d("derp", String.valueOf(error.networkResponse.statusCode));
                        }
                    }
                }
        );
    }

}
