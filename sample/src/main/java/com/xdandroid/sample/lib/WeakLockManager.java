package com.xdandroid.sample.lib;

import android.content.Context;
import android.os.PowerManager;

import com.xdandroid.sample.App;

/**
 * Created by qGod on 2017/7/17
 * Thank you for watching my code
 * 锁屏管理者
 */

public class WeakLockManager {
    PowerManager.WakeLock wakeLock = null;

    public void acquire() {
        try {
            if (null == wakeLock) {
                PowerManager pm = (PowerManager) App.getmContext().getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,"MyService");
//                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TraceServiceImpl.class.getName());
                if (null != wakeLock) {
                    wakeLock.acquire();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void releease() {
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
