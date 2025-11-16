package com.example.project2group1.core;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.project2group1.data.User;


public class Prefs {
    private static final String PREF_NAME = "triviago_prefs";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_ADMIN = "is_admin";

    private final SharedPreferences sp;

    public Prefs(Context context) {
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLoggedIn(User user) {
        if (user == null) return;
        sp.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_USERNAME, user.username)
                .putBoolean(KEY_IS_ADMIN, user.isAdmin)
                .apply();
    }

    public boolean isLoggedIn() {
        return sp.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getUsername() {
        return sp.getString(KEY_USERNAME, null);
    }

    public boolean isAdmin() {
        return sp.getBoolean(KEY_IS_ADMIN, false);
    }

    public void logout() {
        sp.edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .remove(KEY_USERNAME)
                .remove(KEY_IS_ADMIN)
                .apply();
    }
}
