package com.xdandroid.sample.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xdandroid.sample.MainActivity;


/**
 * Created by qGod on 2017/7/4
 * Thank you for watching my code
 * 开机启动
 */
public class UndeadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())||
                Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())||
                Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
            Log.d("undead", "开机启动");
            //跳转至主页
            Intent service = new Intent(context, MainActivity.class);
            context.startService(service);
            //启动应用,此处填写包名
            Intent intent1 = context.getPackageManager().getLaunchIntentForPackage("com.xdandroid.sample");
            context.startActivity(intent1);
        }
    }
}
