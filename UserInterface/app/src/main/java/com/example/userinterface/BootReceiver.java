package com.example.userinterface;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set alarms
            setAlarm(context);
        }
    }

    public void setAlarm(Context context){

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CheckPlantlistForNotificationService.class);                                           //<<<---- Modify intent to service
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//      long startTime = System.currentTimeMillis();      <- Works for init just after boot
        Calendar hourToTrigger = Calendar.getInstance();
        hourToTrigger.setTimeInMillis(System.currentTimeMillis());
        hourToTrigger.set(Calendar.HOUR_OF_DAY, 17);
        hourToTrigger.set(Calendar.MINUTE, 21);
        hourToTrigger.set(Calendar.SECOND, 0);

        long intervalTime = 24* 60 * 60 * 1000;     //one day

        String message = "Start service use repeat alarm. ";
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, hourToTrigger.getTimeInMillis(), intervalTime, pendingIntent);

    }
}
