package com.gm.blackkite.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AlertDialog;

import com.gm.blackkite.R;
import com.gm.blackkite.activity.MainActivity;


public class Session {
    public static final String PREFER_NAME = "eKart";
    final int PRIVATE_MODE = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _activity;


    public Session(Context activity) {
        try {
            this._activity = activity;
            pref = _activity.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
            editor = pref.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCount(String id, int value, Context activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(id, value);
        editor.apply();
    }

    public int getCount(String id, Context activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getInt(id, 0);
    }

    public String getData(String id) {
        return pref.getString(id, "");
    }

    public String getCoordinates(String id) {
        return pref.getString(id, "0");
    }

    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    public void setBoolean(String id, boolean val) {
        editor.putBoolean(id, val);
        editor.commit();
    }

    public boolean getBoolean(String id) {
        return pref.getBoolean(id, false);
    }

    public void createUserLoginSession(String profile, String fcmId, String id, String name, String email, String mobile, String password, String referCode) {
        editor.putBoolean(Constant.IS_USER_LOGIN, true);
        editor.putString(Constant.FCM_ID, fcmId);
        editor.putString(Constant.ID, id);
        editor.putString(Constant.NAME, name);
        editor.putString(Constant.EMAIL, email);
        editor.putString(Constant.MOBILE, mobile);
        editor.putString(Constant.PASSWORD, password);
        editor.putString(Constant.REFERRAL_CODE, referCode);
        editor.putString(Constant.PROFILE, profile);
        editor.commit();
    }

    public void setUserData(String user_id, String name, String email, String country_code, String profile, String mobile, String balance, String referral_code, String friends_code, String fcm_id, String status) {

        editor.putString(Constant.USER_ID, user_id);
        editor.putString(Constant.NAME, name);
        editor.putString(Constant.EMAIL, email);
        editor.putString(Constant.COUNTRY_CODE, country_code);
        editor.putString(Constant.PROFILE, profile);
        editor.putString(Constant.MOBILE, mobile);
        editor.putString(Constant.BALANCE, balance);
        editor.putString(Constant.REFERRAL_CODE, referral_code);
        editor.putString(Constant.FRIEND_CODE, friends_code);
        editor.putString(Constant.FCM_ID, fcm_id);
        editor.putString(Constant.STATUS, status);
        editor.commit();
    }

    public void logoutUser(Activity activity) {
        editor.clear();
        editor.commit();

        new Session(_activity).setBoolean("is_first_time", true);

        Intent i = new Intent(activity, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Constant.FROM, "");
        activity.startActivity(i);
        activity.finish();
    }

    public void logoutUserConfirmation(final Activity activity) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(_activity);
        alertDialog.setTitle(R.string.logout);
        alertDialog.setMessage(R.string.logout_msg);
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton(R.string.yes, (dialog, which) -> {
            editor.clear();
            editor.commit();

            new Session(_activity).setBoolean("is_first_time", true);

            Intent i = new Intent(activity, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(Constant.FROM, "");
            activity.startActivity(i);
            activity.finish();
        });

        alertDialog.setNegativeButton(R.string.no, (dialog, which) -> alertDialog1.dismiss());
        // Showing Alert Message
        alertDialog.show();

    }

}