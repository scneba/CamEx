package com.thestk.camex.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.thestk.camex.models.UserModel;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Neba on 10-Oct-17.
 */

public class UserSharedPreference {
    private static final String PREFERENCE_NAME = "user_info";
    private static final String IS_LOGGED_IN = "is_loggedin";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String EMAIL = "email";
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    public UserSharedPreference(Context context) {

        pref = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = pref.edit();
        if (!pref.contains(IS_LOGGED_IN)) {
            editor.putBoolean(IS_LOGGED_IN, false);
        }
        if (!pref.contains(USER_ID)) {
            editor.putInt(USER_ID, -1);
        }
        if (!pref.contains(USER_NAME)) {
            editor.putString(USER_NAME, "default");
        }
        if (!pref.contains(EMAIL)) {
            editor.putString(EMAIL, "default@gmail.com");
        }
    }

    public boolean loginUser(UserModel userModel) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putInt(USER_ID, userModel.getUser_id());
        editor.putString(USER_NAME, userModel.getUser_name());
        editor.putString(EMAIL, userModel.getEmail());
        if (editor.commit()) {
            return true;
        } else {
            return false;
        }
    }

    public UserModel getUserData() {
        int user_id = pref.getInt(USER_ID, -1);
        String username = pref.getString(USER_NAME, null);
        String email = pref.getString(EMAIL, null);
        UserModel userModel = new UserModel(user_id, username, email);
        return userModel;
    }

    public boolean logoutUser() {
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putInt(USER_ID, -1);
        editor.putString(USER_NAME, "default");
        editor.putString(EMAIL, "default@gmail.com");
        if (editor.commit()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }


}
