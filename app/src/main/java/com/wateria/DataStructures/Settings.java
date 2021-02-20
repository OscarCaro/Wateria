package com.wateria.DataStructures;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

    private SharedPreferences prefs;

    private final String notifEnabledKey = "notif_enabled";
    private final boolean notifEnabledDefault = true;

    private final String notifHourKey = "notif_hour";
    private final int notifHourDefault = 18;

    private final String notifMinuteKey = "notif_minute";
    private final int notifMinuteDefault = 00;

    private final String notifRepetIntervalKey = "notif_repetition";
    private final int notifRepetIntervalDefault = 1;


    public Settings(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public int getNotifHour(){
        return prefs.getInt(notifHourKey, notifHourDefault);
    }

    public void setNotifHour(int newValue){
        if (newValue >= 0 && newValue <= 23){
            prefs.edit().putInt(notifHourKey, newValue).apply();
        }
    }

    public int getNotifMinute(){
        return prefs.getInt(notifMinuteKey, notifMinuteDefault);
    }

    public void setNotifMinute(int newValue){
        if (newValue >= 0 && newValue <= 59){
            prefs.edit().putInt(notifMinuteKey, newValue).apply();
        }
    }

    public int getNotifSecond(){
        return 0;   // Default value
    }

    public int getNotifRepetInterval(){
        return prefs.getInt(notifRepetIntervalKey, notifRepetIntervalDefault);
    }

    public void setNotifRepetInterval(int newValue){
        if (newValue >= 1 && newValue <= 23){
            prefs.edit().putInt(notifRepetIntervalKey, newValue).apply();
        }
    }

    public boolean getNotifEnabled(){
        return prefs.getBoolean(notifEnabledKey, notifEnabledDefault);
    }

    public void setNotifEnabled(boolean value){
        prefs.edit().putBoolean(notifEnabledKey, value).apply();
    }
}
