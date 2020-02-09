package com.example.wateria;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.wateria.Services.CheckPlantlistForNotificationService;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    private SharedPreferences prefs;

    private Integer HourToTrigger;            // To be stored in sharedPrefs and set in the settings activity
    private Integer MinuteToTrigger;
    private Integer SecondToTrigger;

    private String sharedPrefHourKey;
    private String sharedPrefMinuteKey;

    static final String WATER_SINGLE_PLANT_SERVICE_PUTEXTRA_PLANT_NAME = "extra_plant_name";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setAlarm(context);
        }
    }

    public void setAlarm(Context context){
        getTriggerTimeFromPreferences(context);     //Default 16:00

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CheckPlantlistForNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar now = Calendar.getInstance();

        Calendar triggerMoment = Calendar.getInstance();
        triggerMoment.set(Calendar.HOUR_OF_DAY, HourToTrigger);
        triggerMoment.set(Calendar.MINUTE, MinuteToTrigger);
        triggerMoment.set(Calendar.SECOND, SecondToTrigger);

        if (now.compareTo(triggerMoment) > 0){
            // Set the tomorrows's alarm because today's moment have already passed
            triggerMoment.add(Calendar.DATE, 1);
        }

        long intervalTime = 24 * 60 * 60 * 1000;     //one day = 24* 60 * 60 * 1000

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerMoment.getTimeInMillis(), intervalTime, pendingIntent);
    }

    public void getTriggerTimeFromPreferences (Context broadcastReceiverContext){
        Context appContext = broadcastReceiverContext.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(appContext);

        sharedPrefHourKey = appContext.getResources().getString(R.string.shared_prefs_hour_key);
        sharedPrefMinuteKey = appContext.getResources().getString(R.string.shared_prefs_minute_key);

        HourToTrigger = prefs.getInt(sharedPrefHourKey, 18);
        MinuteToTrigger = prefs.getInt(sharedPrefMinuteKey, 0);
        SecondToTrigger = 0;
    }
}
