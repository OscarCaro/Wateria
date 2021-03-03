package com.wateria.JobSchedulers;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.wateria.DataStructures.Plant;
import com.wateria.DataStructures.PlantList;
import com.wateria.DataStructures.Settings;
import com.wateria.Notifications.NotificationClass;

import java.util.ArrayList;

public class RemindLaterNotificationJobService extends JobService {

    public static final int REMIND_LATER_JOB_ID = 1;

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

        // No rescheduling

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
