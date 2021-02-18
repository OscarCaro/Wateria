package com.example.wateria.JobSchedulers;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;

import com.example.wateria.DataStructures.Plant;
import com.example.wateria.DataStructures.PlantList;
import com.example.wateria.Notifications.NotificationClass;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;

public class NotificationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        // Create the new notification
        AndroidThreeTen.init(getApplicationContext());

        Context context = getApplicationContext();

        // Get a sublist filled with the plants that need to be watered (0 days remaining)
        ArrayList<Plant> zeroDaysList = PlantList.getInstance(this).get0daysRemSublist();

        if (zeroDaysList.size() > 0) {        // There are plants that need to be watered today
            //Compute notifications
            NotificationClass.createNotificationChannel(context);
            NotificationClass.pushNotification(context, zeroDaysList);
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
