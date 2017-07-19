package com.xdandroid.sample;

import android.app.*;
import android.content.*;
import android.util.Log;

import com.xdandroid.sample.lib.DaemonEnv;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MainActivity"," app");
        DaemonEnv.initialize(this, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        try {
            startService(new Intent(this, TraceServiceImpl.class));
        } catch (Exception ignored) {
        }
    }
}
