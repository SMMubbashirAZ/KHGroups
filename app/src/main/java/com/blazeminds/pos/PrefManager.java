package com.blazeminds.pos;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "bms-welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_FIRST_TIME_NOTIFY = "IsFirstTimeNotify";
    // shared pref mode
    private int PRIVATE_MODE = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    
    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
    public String isFirstTimeNotify() {
        return pref.getString(IS_FIRST_TIME_NOTIFY, "");
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setFirstTimeNotification(String isFirstTime) {
        editor.putString(IS_FIRST_TIME_NOTIFY, isFirstTime);
        editor.commit();
    }
    
}
