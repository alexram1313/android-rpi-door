package com.arcompware.doornotifiercontroller;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by alexram1313 on 8/8/17.
 */

public class VolleyMgr {
    public static void sendGetRequest(Context context,
                                   String url,
                                   Response.Listener<String> responseListener,
                                   Response.ErrorListener errorListener){

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                responseListener, errorListener);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
