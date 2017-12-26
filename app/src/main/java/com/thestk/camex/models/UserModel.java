package com.thestk.camex.models;

/**
 * Created by Neba on 10-Oct-17.
 */

public class UserModel {
    private int user_id;
    private String user_name;
    private String email;

    public UserModel(int user_id, String user_name, String email) {
        this.email = email;
        this.user_id = user_id;
        this.user_name = user_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getEmail() {
        return email;
    }
}
