package com.xdandroid.sample;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xdandroid.sample.lib.DaemonEnv;

/**
 * Application
 */
public class App extends Application {

    public static App instance;

    public static Context getmContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MainActivity"," app启动");
        instance=this;
        DaemonEnv.initialize(this, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        try {
            startService(new Intent(this, TraceServiceImpl.class));
        } catch (Exception ignored) {
        }
    }
}
