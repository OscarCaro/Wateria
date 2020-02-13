package com.example.wateria.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.wateria.DataStructures.PlantList;
import com.example.wateria.NotificationClass;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class CheckPlantlistForNotificationService extends Service {

    private PlantList plantList;

    public CheckPlantlistForNotificationService(){
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
        String message = "RunAfterBootService onCreate() method.";                // <--- To be deleted
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        //android.os.Debug.waitForDebugger();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();

        plantList = new PlantList(this);
        plantList.loadFromPrefs(false);

        if (plantList.getSize() > 0){
            if (plantList.getNumOfZeroDaysRemPlants() > 0) {        // There are plants that need to be watered today
                //Compute notifications
                NotificationClass.createNotificationChannel(context);
                NotificationClass.pushNotification(context, plantList);
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}


