package com.example.anuj.auth.SessionManagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.anuj.auth.MainActivity;
import com.example.anuj.auth.model.reciptModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Raj on 26-Sep-18.
 */

public class SessionManager {

    static int seatcount = 0;
    // Shared Preferences
    static SharedPreferences pref;

    // Editor for Shared preferences
    static SharedPreferences.Editor editor;

    // Context
    static Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;
    static Gson gson = new Gson();

    public static final String PREF_NAME = "LogedInUser";
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_Name = "name";
    public static final String KEY_Email = "email";
    public static final String KEY_Pno = "PhoneNo";
    public static final String KEY_Dob = "DOB";
    public static final String KEY_Details = "details";
    public static final String KEY_MemberShipNo = "membershipno";
    public static final String KEY_Pwd = "pwd";
    public static final String KEY_Status = "status";
    public static final String KEY_Type = "type";


    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static List<reciptModel> getReciptList() throws JSONException {
        List<reciptModel> list = new ArrayList<>();
        for (int i = 0; i < seatcount; i++) {
            list.add(getOneRecipt(i));
        }
        return list;
    }

    public static reciptModel getOneRecipt(int reciptId) throws JSONException {
        String json = pref.getString("recipt" + String.valueOf(reciptId), null);
        JSONObject properties = new JSONObject(json);

        // Extract out the title, number of people, and perceived strength values
        String userID = properties.getString("userID");
        String timestamp = properties.getString("timestamp");
        String movieNmae = properties.getString("movieNmae");
        String date = properties.getString("date");
        String Movietime = properties.getString("Movietime");
        List<String> list = new ArrayList<>();
        JSONArray jsonArray = properties.getJSONArray("seatList");
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        int cost = properties.getInt("cost");
        // Create a new {@link Event} object
        reciptModel r = new reciptModel();
        r.setCost(cost);
        r.setSeatsList(list);
        r.setTimestamp(timestamp);
        r.setMovieNmae(movieNmae);
        r.setDate(date);
        r.setMovietime(Movietime);
        r.setUserID(userID);
        return r;
    }

    public static void AddRecipt(reciptModel r) {
        String json = gson.toJson(r);
        editor.putString("recipt" + String.valueOf(seatcount), json);
        seatcount++;
        editor.commit();
    }

    //delete recipt here ;;;;

    public static String getDob() {
        return pref.getString(KEY_Dob, null);
    }

    public static void setDob(String name) {
        editor.putString(KEY_Dob, name);
        editor.commit();
    }

    public static String getPno() {
        return pref.getString(KEY_Pno, null);
    }

    public static void setPno(String name) {
        editor.putString(KEY_Pno, name);
        editor.commit();
    }

    public static String getMemberShipNo() {
        return pref.getString(KEY_MemberShipNo, null);
    }

    public static void setMemberShipNo(String name) {
        editor.putString(KEY_MemberShipNo, name);
        editor.commit();
    }

    public static String getType() {
        return pref.getString(KEY_Type, null);
    }

    public static void setType(String name) {
        editor.putString(KEY_Type, name);
        editor.commit();
    }

    public static String getStatus() {
        return pref.getString(KEY_Status, null);
    }

    public static void setStatus(String name) {
        editor.putString(KEY_Status, name);
        editor.commit();
    }

    public static String getName() {
        return pref.getString(KEY_Name, null);
    }

    public static void setName(String name) {
        editor.putString(KEY_Name, name);
        editor.commit();
    }

    public static String getEmail() {
        return pref.getString(KEY_Email, null);
    }

    public static void setEmail(String email) {
        editor.putString(KEY_Email, email);
        editor.commit();
    }


    public static String getPwd() {
        return pref.getString(KEY_Pwd, null);
    }

    public static void setPwd(String pwd) {
        editor.putString(KEY_Pwd, pwd);
        editor.commit();
    }


    public static String getDetails() {
        return pref.getString(KEY_Details, null);
    }

    public static void setDetails(String details) {
        editor.putString(KEY_Details, details);
        editor.commit();
    }


    public void createLoginSession(String pno, String dob, String MemberShipNo, String pwd, String email, String name, String status, String Type, String details) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_Name, name);
        editor.putString(KEY_Email, email);
        editor.putString(KEY_Pno, pno);
        editor.putString(KEY_Dob, dob);
        editor.putString(KEY_Details, details);
        editor.putString(KEY_MemberShipNo, MemberShipNo);
        editor.putString(KEY_Pwd, pwd);
        editor.putString(KEY_Status, status);
        editor.putString(KEY_Type, Type);
        // commit changes
        editor.commit();
    }


    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_Email, pref.getString(KEY_Email, null));
        user.put(KEY_MemberShipNo, pref.getString(KEY_MemberShipNo, null));
        user.put(KEY_Pwd, pref.getString(KEY_Pwd, null));
        user.put(KEY_Email, pref.getString(KEY_Email, null));
        user.put(KEY_Name, pref.getString(KEY_Name, null));
        user.put(KEY_Dob, pref.getString(KEY_Dob, null));
        user.put(KEY_Pno, pref.getString(KEY_Pno, null));
        user.put(KEY_Status, pref.getString(KEY_Status, null));
        user.put(KEY_Type, pref.getString(KEY_Type, null));
        user.put(KEY_Details, pref.getString(KEY_Details, null));
        // return user
        return user;
    }

    //    public static final String PREF_NAME = "LogedInUser";
//    public static final String IS_LOGIN = "IsLoggedIn";
//    public static final String KEY_Name = "name";
//    public static final String KEY_Email = "email";
//    public static final String KEY_Pno = "PhoneNo";
//    public static final String KEY_Dob = "DOB";
//    public static final String KEY_Details = "details";
//    public static final String KEY_MemberShipNo = "membershipno";
//    public static final String KEY_Pwd = "pwd";
//    public static final String KEY_Status = "status";
//    public static final String KEY_Type = "type";

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);

    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


}