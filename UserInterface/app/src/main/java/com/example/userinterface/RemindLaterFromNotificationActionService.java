package com.example.userinterface;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class RemindLaterFromNotificationActionService extends Service {

    private SharedPreferences prefs;
    private String sharedPrefDelayTimeKey;

    public RemindLaterFromNotificationActionService(){
    }

    @Override
    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        AndroidThreeTen.init(getApplicationContext());
        String message = "RemindLaterService onCreate() method.";                // <--- To be deleted
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        //android.os.Debug.waitForDebugger();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();

        // 1º Get elapse time from memory
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefDelayTimeKey = context.getResources().getString(R.string.shared_prefs_delay_time_key);
        Integer hoursToDelay = prefs.getInt(sharedPrefDelayTimeKey, 2);

        // Set AlarmManager to trigger notification in DelayTime
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, CheckPlantlistForNotificationService.class);
        PendingIntent notificationPendingIntent = PendingIntent.getService(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hoursToDelay);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notificationPendingIntent);

        String message = "Reminder postponed for " + hoursToDelay + " hours";            //<--- To be erased
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
