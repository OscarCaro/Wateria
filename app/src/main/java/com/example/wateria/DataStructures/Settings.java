package com.example.wateria.DataStructures;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

    private Context appContext;
    private SharedPreferences prefs;

    private String notifHourKey = "notif_hour";
    private int notifHourDefault = 18;

    private String notifMinuteKey = "notif_minute";
    private int notifMinuteDefault = 00;

    private String notifRepetIntervalKey = "notif_repetition";
    private int notifRepetIntervalDefault = 1;

    public Settings(Context context){
        this.appContext = context.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public int getNotifHour(){
        return prefs.getInt(notifHourKey, notifHourDefault);
    }

    public int getNotifMinute(){
        return prefs.getInt(notifMinuteKey, notifMinuteDefault);
    }

    public int getNotifSecond(){
        return 0;
    }

    public int getNotifRepetInterval(){
        return prefs.getInt(notifRepetIntervalKey, notifRepetIntervalDefault);
    }
}
