package com.example.wateria.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
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

public class WaterSinglePlantFromNotificationActionService extends Service {

    static final String WATER_SINGLE_PLANT_SERVICE_PUTEXTRA_PLANT_NAME = "extra_plant_name";

    public WaterSinglePlantFromNotificationActionService(){
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
        //android.os.Debug.waitForDebugger();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 1º Get plantList from memory
        PlantList plantList = new PlantList(this);
        plantList.loadFromPrefs(false);

        // 2º Get specific plant name
        String plantName = intent.getStringExtra(WATER_SINGLE_PLANT_SERVICE_PUTEXTRA_PLANT_NAME);

        // 3º Water specific plant and save(if found)
        Integer index = plantList.findByName(plantName);
        if (index != -1){
            plantList.waterPlant(index);
            plantList.saveToPrefs();
        }

        // 4º Remove notification from status bar              TODO: remove notification just on the oncreate, for better user exp
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancel(NotificationClass.notificationId);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
