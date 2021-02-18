package com.example.wateria.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationManagerCompat;
import android.widget.Toast;

import com.example.wateria.DataStructures.Settings;
import com.example.wateria.JobSchedulers.NotificationJobService;
import com.example.wateria.Notifications.NotificationClass;
import com.example.wateria.R;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Calendar;

public class RemindLaterFromNotificationActionService extends Service {

    private Context appContext;
    private static final int JOB_ID = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        appContext = getApplicationContext();
        AndroidThreeTen.init(appContext);

        // Remove notification from status bar
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(appContext);
        notificationManager.cancel(NotificationClass.notificationId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 1º Get elapse time from memory
        int hoursToDelay = new Settings(appContext).getNotifRepetInterval();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                //.setOverrideDeadline(calendar.getTimeInMillis())
                //.setRequiresDeviceIdle(false)
                //.setRequiresCharging(false)
                //.setPersisted(true)
                //.setBackoffCriteria(6000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setMinimumLatency(hoursToDelay * 60 * 1000);

        JobInfo jobInfo = builder.build();
        jobScheduler.schedule(jobInfo);

        String message = getResources().getString(R.string.notif_postpone_toast_message) + " "
                + hoursToDelay + " " + getResources().getQuantityString(R.plurals.hours, hoursToDelay);
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
        /*

        // 1º Get elapse time from memory
        int hoursToDelay = new Settings(appContext).getNotifRepetInterval();

        // Set AlarmManager to trigger notification in DelayTime
        AlarmManager alarmManager = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(appContext, CheckPlantlistForNotificationService.class);
        PendingIntent notificationPendingIntent = PendingIntent.getService(appContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hoursToDelay);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notificationPendingIntent);

        String message = "Reminder postponed for " + hoursToDelay + " hour";
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();

         */

        return START_NOT_STICKY;
    }

}
