package com.example.wateria.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.wateria.DataStructures.Plant;
import com.example.wateria.DataStructures.PlantList;
import com.example.wateria.Notifications.NotificationClass;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;

public class CheckPlantlistForNotificationService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        AndroidThreeTen.init(getApplicationContext());
//        android.os.Debug.waitForDebugger();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();

        // Get a sublist filled with the plants that need to be watered (0 days remaining)
        ArrayList<Plant> zeroDaysList = PlantList.getInstance(this).get0daysRemSublist();

        if (zeroDaysList.size() > 0) {        // There are plants that need to be watered today
            //Compute notifications
            NotificationClass.createNotificationChannel(context);
            NotificationClass.pushNotification(context, zeroDaysList);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}


