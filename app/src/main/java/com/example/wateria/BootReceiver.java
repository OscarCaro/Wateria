package com.example.wateria;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.wateria.DataStructures.Settings;
import com.example.wateria.Services.CheckPlantlistForNotificationService;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    private static final int requestCode = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setAlarm(context);
        }
    }

    public static void setAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CheckPlantlistForNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long intervalTime = 24 * 60 * 60 * 1000;     //one day = 24* 60 * 60 * 1000

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, computeTriggerTime(context), intervalTime, pendingIntent);
    }

    public static void cancelAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CheckPlantlistForNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    private static long computeTriggerTime(Context context){
        Settings settings = new Settings(context);

        Calendar now = Calendar.getInstance();

        Calendar triggerMoment = Calendar.getInstance();
        triggerMoment.set(Calendar.HOUR_OF_DAY, settings.getNotifHour());
        triggerMoment.set(Calendar.MINUTE, settings.getNotifMinute());
        triggerMoment.set(Calendar.SECOND, settings.getNotifSecond());

        if (now.compareTo(triggerMoment) > 0){
            // Set the tomorrows's alarm because today's moment have already passed
            triggerMoment.add(Calendar.DATE, 1);
        }

        return triggerMoment.getTimeInMillis();
    }
}
