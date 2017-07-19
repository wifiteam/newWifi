package com.xdandroid.sample.lib;

import android.annotation.*;
import android.app.job.*;
import android.content.*;
import android.os.*;

/**
 * Android 5.0+ 使用的 JobScheduler.
 * 运行在 :watch 子进程中.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        if (!DaemonEnv.sInitialized) return false;
        try {startService(new Intent(DaemonEnv.sApp, DaemonEnv.sServiceClass));} catch (Exception ignored) {}
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
