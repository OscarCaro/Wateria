package com.example.userinterface;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

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
        getTriggerTimeFromPreferences(context);     //Default 18:00

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CheckPlantlistForNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) > HourToTrigger
                || ( calendar.get(Calendar.HOUR_OF_DAY) == HourToTrigger && calendar.get(Calendar.MINUTE) > MinuteToTrigger )
                || ( calendar.get(Calendar.HOUR_OF_DAY) == HourToTrigger && calendar.get(Calendar.MINUTE) == MinuteToTrigger && calendar.get(Calendar.SECOND) > SecondToTrigger )){

            // Set the tomorrows's alarm because today's moment have already passed
            calendar.set(Calendar.HOUR_OF_DAY, HourToTrigger);
            calendar.set(Calendar.MINUTE, MinuteToTrigger);
            calendar.set(Calendar.SECOND, SecondToTrigger);
            calendar.add(Calendar.DATE, 1);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, HourToTrigger);
            calendar.set(Calendar.MINUTE, MinuteToTrigger);
            calendar.set(Calendar.SECOND, SecondToTrigger);
        }

        long intervalTime = 10 * 60 * 1000;     //one day = 24* 60 * 60 * 1000

        String message = "Start service use repeat alarm. ";            //<--- To be erased
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

//        NotificationClass.createNotificationChannel(context);           //<--- To be erased
//        NotificationClass.pushNotification(context);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalTime, pendingIntent);

        message = "Alarm set at" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " of " + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.MONTH);            //<--- To be erased
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }

    public void getTriggerTimeFromPreferences (Context broadcastReceiverContext){
        Context appContext = broadcastReceiverContext.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(appContext);

        sharedPrefHourKey = appContext.getResources().getString(R.string.shared_prefs_hour_key);
        sharedPrefMinuteKey = appContext.getResources().getString(R.string.shared_prefs_minute_key);

        HourToTrigger = prefs.getInt(sharedPrefHourKey, 19);
        MinuteToTrigger = prefs.getInt(sharedPrefMinuteKey, 10);
        SecondToTrigger = 0;
    }
}
