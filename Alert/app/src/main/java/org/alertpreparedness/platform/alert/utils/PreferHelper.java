package org.alertpreparedness.platform.alert.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PreferHelper {

    private static SharedPreferences mSharedPreferences;

    public static SharedPreferences getInstance(Context context) {

        if (mSharedPreferences==null) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            return mSharedPreferences;
        }
    }

    public static void putString(Context context, String key, String value) {
        PreferHelper.getInstance(context).edit().putString(key,value).apply();
    }

    public static String getString(Context context, String key) {
        return PreferHelper.getInstance(context).getString(key,"");
    }

    public static void putBoolean(Context context, String key, boolean value) {
        PreferHelper.getInstance(context).edit().putBoolean(key,value).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        return PreferHelper.getInstance(context).getBoolean(key, false);
    }

    public static void putInt(Context context, String key, int value) {
        PreferHelper.getInstance(context).edit().putInt(key,value).apply();
    }

    public static int getInt(Context context, String key) {
        return PreferHelper.getInstance(context).getInt(key, -1);
    }

    public static String getToken(Context context) {
        return PreferHelper.getInstance(context).getString("DEVICE_TOKEN", "testing_null_id");
    }

    public static void setToken(Context context, String token) {
        PreferHelper.getInstance(context).edit().putString("DEVICE_TOKEN", token).apply();
    }

    public static List<String> getListOfString(Context context, String key) {
        Set<String> stringSet = PreferHelper.getInstance(context).getStringSet(key, new HashSet<String>());
        return new ArrayList<>(stringSet);
    }

    public static void setListOfString(Context context, String key, List<String> strings) {
        Set<String> stringSet = new HashSet<>(strings);
        PreferHelper.getInstance(context).edit().putStringSet(key, stringSet).apply();
    }

    public static void deleteString(Context context, String key) {
        PreferHelper.getInstance(context).edit().remove(key).apply();
    }

}
