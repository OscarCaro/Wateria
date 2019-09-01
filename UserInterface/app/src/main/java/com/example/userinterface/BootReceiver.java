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

    static final Integer HourToTrigger = 18;            // To be stored in sharedPrefs
    static final Integer MinuteToTrigger = 37;
    static final Integer SecondToTrigger = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setAlarm(context);
        }
    }

    public void setAlarm(Context context){

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CheckPlantlistForNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//      long startTime = System.currentTimeMillis();      <- Works for init just after boot
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
//        NotificationClass.pushNotificationFirstAttempt(context);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalTime, pendingIntent);

        message = "Alarm set at" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " of " + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.MONTH);            //<--- To be erased
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }
}
