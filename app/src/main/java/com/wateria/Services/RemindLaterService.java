package com.wateria.Services;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationManagerCompat;
import android.widget.Toast;

import com.wateria.DataStructures.Settings;
import com.wateria.JobSchedulers.NotificationJobService;
import com.wateria.JobSchedulers.RemindLaterNotificationJobService;
import com.wateria.Notifications.NotificationClass;
import com.wateria.R;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class RemindLaterService extends Service {

    private Context appContext;

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
        JobInfo.Builder builder = new JobInfo.Builder(RemindLaterNotificationJobService.REMIND_LATER_JOB_ID, serviceName)
                .setPersisted(true)
                .setMinimumLatency(hoursToDelay * 60 * 60 * 1000);

        JobInfo jobInfo = builder.build();
        jobScheduler.schedule(jobInfo);

        String message = getResources().getString(R.string.notif_postpone_toast_message) + " "
                + hoursToDelay + " " + getResources().getQuantityString(R.plurals.hours, hoursToDelay);
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();

        return START_NOT_STICKY;
    }

}
