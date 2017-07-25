package com.xdandroid.sample.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xdandroid.sample.MainActivity;
import com.xdandroid.sample.TraceServiceImpl;

public class WakeUpReceiver extends BroadcastReceiver {

    /**
     * 向 WakeUpReceiver 发送带有此 Action 的广播, 即可在不需要服务运行的时候取消 Job / Alarm / Subscription.
     */
    protected static final String ACTION_CANCEL_JOB_ALARM_SUB = "com.xdandroid.hellodaemon.CANCEL_JOB_ALARM_SUB";

    /**
     * 监听 8 种系统广播 :
     * CONNECTIVITY\_CHANGE, USER\_PRESENT, ACTION\_POWER\_CONNECTED, ACTION\_POWER\_DISCONNECTED,
     * BOOT\_COMPLETED, MEDIA\_MOUNTED, PACKAGE\_ADDED, PACKAGE\_REMOVED.
     * 在网络连接改变, 用户屏幕解锁, 电源连接 / 断开, 系统启动完成, 挂载 SD 卡, 安装 / 卸载软件包时拉起 Service.
     * Service 内部做了判断，若 Service 已在运行，不会重复启动.
     * 运行在:watch子进程中.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && ACTION_CANCEL_JOB_ALARM_SUB.equals(intent.getAction())) {
            WatchDogService.cancelJobAlarmSub();
            return;
        }
        DaemonEnv.initialize(context, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);

        if (!DaemonEnv.sInitialized) return;
        try {
            context.startService(new Intent(context, TraceServiceImpl.class));// TODO
        } catch (Exception ignored) {
        }
    }

    public static class WakeUpAutoStartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DaemonEnv.initialize(context, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);

            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                    Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction()) ||
                    Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
                Log.d("MainActivity", "开机启动");
                Log.d("MainActivity", "------------------------------------");
                //跳转至主页
                Intent service = new Intent(context, MainActivity.class);
                context.startService(service);
                //启动应用,此处填写包名
                Intent intent1 = context.getPackageManager().getLaunchIntentForPackage("com.xdandroid.sample");
                context.startActivity(intent1);
            }

            if (!DaemonEnv.sInitialized) return;
            try {
                context.startService(new Intent(context, TraceServiceImpl.class));// TODO
            } catch (Exception ignored) {
            }
        }
    }
}
