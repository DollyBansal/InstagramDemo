package com.dolly.instagramdemo.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// The authentication token is saved persistently using SharedPreferences.
// In future, to make things more robust, one can encrypt the token before saving.
// More sophisticated methods can also be used - please see the link below.
// https://stackoverflow.com/questions/42914230/should-i-store-user-object-with-authentication-token-attribute-in-sharedpreferen
public class SharedPreferencesUtils {

    public static String getSharedPreferencesToken(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("access_token", "");
    }

    public static void saveDataInSharedPreferences(Context context, String access_token) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString("access_token", access_token).commit();
    }

    public static void clear(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
