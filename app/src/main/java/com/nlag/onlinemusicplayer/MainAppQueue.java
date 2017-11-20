package com.nlag.onlinemusicplayer;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by nlag on 11/21/17.
 */

public class MainAppQueue extends Application {
    RequestQueue queue;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(getApplicationContext());
    }

    public RequestQueue getQueue() {
        return queue;
    }
}
