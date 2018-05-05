package com.example.staysafe.data.session;


import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    public String id="";
    public String name="";
    public String email="";
    public String dob="";
    public long contact_no= 0;
    public String gender="";

    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared pref file name
    private static final String PREF_NAME = "USER";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_DOB = "dob";
    public static final String KEY_CONTACT_NO = "contact_no";
    public static final String KEY_GENDER = "gender";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create Login Session
    public void createLoginSession(String id, String name, String email, String dob, long contact_no, String gender){

        // Storing values in prefs
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_DOB, dob);
        editor.putLong(KEY_CONTACT_NO, contact_no);
        editor.putString(KEY_GENDER, gender);
        // commit changes
        editor.commit();
        userDetails();
    }

    public void userDetails()
    {
        id = pref.getString(KEY_ID, "");
        name = pref.getString(KEY_NAME,"");
        email = pref.getString(KEY_EMAIL,"");
        dob = pref.getString(KEY_DOB,"");
        contact_no = pref.getLong(KEY_CONTACT_NO,0);
        gender = pref.getString(KEY_GENDER,"");
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        // Clearing all data from Shared Preferences
        editor.clear();
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();

        // Resetting default variables
        id = "";
        name = "";
        email = "";
        dob = "";
        contact_no = 0;
        gender = "";
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        userDetails();
        return pref.getBoolean(IS_LOGIN,false);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDob() {
        return dob;
    }

    public long getContact_no() {
        return contact_no;
    }

    public String getGender() {
        return gender;
    }
}

