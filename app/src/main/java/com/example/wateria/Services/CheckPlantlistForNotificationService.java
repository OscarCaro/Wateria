package com.example.wateria.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.wateria.DataStructures.Plant;
import com.example.wateria.DataStructures.PlantList;
import com.example.wateria.NotificationClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

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
            if (plantList.getNumOfNonZeroDaysRemPlants() > 0) {
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


