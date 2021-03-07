package com.wateria.JobSchedulers;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.wateria.DataStructures.Plant;
import com.wateria.DataStructures.PlantList;
import com.wateria.DataStructures.Settings;
import com.wateria.Notifications.NotificationClass;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationJobService extends JobService {

    public static final int NOTIF_JOB_ID = 0;

    @Override
    public boolean onStartJob(JobParameters params) {
        // Create the new notification
        Context context = getApplicationContext();
        AndroidThreeTen.init(context);
        Settings settings = new Settings(context);

        // Get a sublist filled with the plants that need to be watered (0 days remaining)
        ArrayList<Plant> zeroDaysList = PlantList.getInstance(this).get0daysRemSublist();

        if (zeroDaysList.size() > 0 && settings.getNotifEnabled()) {
            //Compute notifications
            NotificationClass.createNotificationChannel(context);
            NotificationClass.pushNotification(context, zeroDaysList);
        }

        // Schedule next one:
        scheduleNextJob(context);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public static void scheduleNextJob(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName serviceName = new ComponentName(context.getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(NOTIF_JOB_ID, serviceName)
                .setPersisted(true)
                .setMinimumLatency(computeTriggerTime(context));

        JobInfo jobInfo = builder.build();
        jobScheduler.schedule(jobInfo);
    }

    public static void cancelScheduledJob(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        if(!jobScheduler.getAllPendingJobs().isEmpty()){
            jobScheduler.cancel(NOTIF_JOB_ID);
            jobScheduler.cancel(RemindLaterNotificationJobService.REMIND_LATER_JOB_ID);
            /*
            for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()){
                if(jobInfo.getId() == NOTIF_JOB_ID){
                    jobScheduler.cancel(NOTIF_JOB_ID);
                }
                else if(jobInfo.getId() == RemindLaterNotificationJobService.REMIND_LATER_JOB_ID)){
                    jobScheduler.cancel(RemindLaterNotificationJobService.REMIND_LATER_JOB_ID);
                }
            }
            */
        }
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

        return triggerMoment.getTimeInMillis() - now.getTimeInMillis();
    }

}
