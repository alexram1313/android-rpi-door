package com.arcompware.doornotifiercontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String ARM = "arm";
    private static final String EVENTS = "events";
    private static final String REG_TOKEN = "regtoken";


    ListView lvEvents;
    ToggleButton toggleArm;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendRequest(EVENTS, null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvEvents = (ListView)findViewById(R.id.lvEvents);
        toggleArm = (ToggleButton)findViewById(R.id.toggleArm);

        sendRequest(EVENTS, null);

        toggleArm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sendRequest(ARM, (b)?"true":"false");
            }
        });

        registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));

        sendSavedToken();
    }

    private void sendSavedToken() {
        SharedPreferences prefs = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token != null) {
            sendRequest(REG_TOKEN, token);
//            Log.d("DERP", token);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    public void refreshEvents(View view){
        sendRequest(EVENTS, null);
    }

    private void updateEvents(String json){
        try {
            JSONArray events = new JSONObject(json).getJSONArray("events");
            ArrayList<HashMap<String, String>> eventsList = new ArrayList<>();
            for (int i = 0; i<events.length(); ++i){
                eventsList.add(new HashMap<String, String>());
                eventsList.get(i).put("event", events.getString(i));
            }

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, eventsList, android.R.layout.simple_list_item_1,
                    new String[] {"event"}, new int[] {android.R.id.text1});
            lvEvents.setAdapter(adapter);

        } catch (JSONException exJ){
            //Do nothing...
        }
    }

    void sendRequest(final String command, @Nullable String param){

        String url = String.format(
                "http://%s:%s/%s",
                getResources().getString(R.string.address),
                getResources().getString(R.string.port),
                command
        );
        if (!command.equals(EVENTS)){
            url += "/"+param;
        }

        final Context me = this;

        VolleyMgr.sendGetRequest(this, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("derp", response);
                        if (command.equals(EVENTS)) updateEvents(response);
                        else sendRequest(EVENTS, null);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Log.d("derp", "Can't connect");
                        }else {
                            Log.d("derp", String.valueOf(error.networkResponse.statusCode));
                        }

                        Toast.makeText(me, "Network error", Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }
}
