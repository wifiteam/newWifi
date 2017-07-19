package com.xdandroid.sample.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * toast工具类
 *
 * @author Administrator
 */
public class ToastUtils {

    public static void toast(Context context, CharSequence str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, int id) {
        Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
    }

    // 可以定制化的制作
    public static void toastCenter(Context context, CharSequence text) {
        Toast toast = new Toast(context);
        TextView view = new TextView(context);
        view.setText(text);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

}
