package com.xdandroid.sample.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {
    private static final String CONFIG = "config";
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        boolean value = preferences.getBoolean( key, false);
        return value;
    }

    public static String getString(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        String value = preferences.getString( key, "");
        return value;
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }
}