package com.example.omi.niggachatdemo.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by omi on 11/8/2016.
 */

public class NiggaChatApplication extends Application {


    //////////////////////////////
    private boolean isUserLoggedIn;
    private String access_token = "";
    private String full_name = "";
    private String email_address = "";
    private boolean isFirstRun = true;


    private SharedPreferences sharedPreferences;
    private String KEY_ACCESS_TOKEN = "access_token";
    private String KEY_FULL_NAME = "full_name";
    private String KEY_IS_USER_LOGGED_IN = "isUserLoggedIn";
    private String KEY_EMAIL_ADDRESS = "email_address";
    private String KEY_FIRST_RUN = "isFirstRun";

    @Override
    public void onCreate() {
        super.onCreate();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.isUserLoggedIn = this.sharedPreferences.getBoolean(KEY_IS_USER_LOGGED_IN,false);
        this.access_token = this.sharedPreferences.getString(KEY_ACCESS_TOKEN,"");
        this.full_name = this.sharedPreferences.getString(KEY_FULL_NAME,"");
        this.email_address = this.sharedPreferences.getString(KEY_EMAIL_ADDRESS,"");
        this.isFirstRun = this.sharedPreferences.getBoolean(KEY_FIRST_RUN,true);

    }


    public boolean isFirstRun()
    {
        return this.isFirstRun;
    }

    public void setFirstRunFalse()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_FIRST_RUN,false);
        editor.commit();
        this.isFirstRun = false;
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public void loggedInUser(String full_name,String email_address)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FULL_NAME, full_name);
        editor.putString(KEY_EMAIL_ADDRESS,email_address);
        editor.putBoolean(KEY_IS_USER_LOGGED_IN,true);
        editor.commit();
        this.isUserLoggedIn = true;
        this.full_name = full_name;
        this.email_address = email_address;
    }


    public String getAccess_token() {
        return access_token;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setAccess_token(String access_token)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, access_token);
        editor.putBoolean(KEY_IS_USER_LOGGED_IN,true);
        editor.commit();
        this.access_token = access_token;
        this.isUserLoggedIn = true;
    }


    public void logoutUser()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, "");
        editor.putString(KEY_FULL_NAME, "");
        editor.putString(KEY_EMAIL_ADDRESS, "");
        editor.putBoolean(KEY_IS_USER_LOGGED_IN,false);
        editor.commit();

        this.isUserLoggedIn = false;
        this.full_name = "";
        this.access_token = "";
        this.email_address = "";
    }

}
