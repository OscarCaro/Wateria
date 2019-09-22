package com.example.userinterface;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.example.userinterface.CheckPlantlistForNotificationService;
import com.example.userinterface.NotificationClass;
import com.example.userinterface.Plant;
import com.example.userinterface.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class WaterSinglePlantFromNotificationActionService extends Service {

    private ArrayList<Plant> plantList = new ArrayList<Plant>();
    private SharedPreferences prefs;
    private String sharedPrefPlantListKey;

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
        String message = "WaterSinglePlantService onCreate() method.";                // <--- To be deleted
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        //android.os.Debug.waitForDebugger();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();
        Boolean ok;

        // 1º Get plantList from memory
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefPlantListKey = context.getResources().getString(R.string.shared_prefs_plantlist_key);
        plantList = getArrayList();
        setDaysRemainingList(plantList);

        // 2º Get specific plant name
        String plantName = intent.getStringExtra(WATER_SINGLE_PLANT_SERVICE_PUTEXTRA_PLANT_NAME);

        // 3º Water specific plant (if found)
        ok = waterNotZeroSinglePlant(plantList, plantName);

        // 4º Save PlantList (only if modified) and notify
        if (ok){
            saveArrayList(plantList);
            String message = plantName + " has been watered";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else {
            String message = plantName + " couldn't be watered";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

        // Remove notification from status bar              TODO: remove notification just on the oncreate, for better user exp
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(NotificationClass.notificationId);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public Boolean waterNotZeroSinglePlant(ArrayList<Plant> plantList, String plantName){
        Boolean ok = false;
        Integer index = getPlantPosition(plantList, plantName);

        if (index != -1){
            ok = true;
            Plant currentPlant = plantList.get(index);
            LocalDate date = LocalDate.now();
            date = date.plusDays(currentPlant.getWateringFrequency());
            currentPlant.setNextWateringDate(date);
            currentPlant.setDaysRemaining(currentPlant.getWateringFrequency());
            plantList.set(index, currentPlant);

            Collections.sort(plantList);
        }

        return ok;
    }

    public Integer getPlantPosition(ArrayList<Plant> plantList, String plantName){
        Integer index = -1;
        for (int i = 0; i < plantList.size(); i++){
            if (plantList.get(i).getPlantName().equals(plantName)){
                index = i;
            }
        }
        return index;
    }

    public ArrayList<Plant> getArrayList(){
        Gson gson = new Gson();
        String json = prefs.getString(sharedPrefPlantListKey, null);
        Type type = new TypeToken<ArrayList<Plant>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveArrayList(ArrayList<Plant> list){
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(sharedPrefPlantListKey, json);
        editor.apply();
    }

    public void setDaysRemainingList (ArrayList<Plant> plantList){
        LocalDate todayDate = LocalDate.now();
        Plant currentPlant;
        int currentDaysRemaining;

        for (int i = 0; i < plantList.size(); i++){
            currentPlant = plantList.get(i);
            currentDaysRemaining = ((int) todayDate.until(currentPlant.getNextWateringDate(), ChronoUnit.DAYS));
            if (currentDaysRemaining < 0){
                currentDaysRemaining = 0;
            }
            currentPlant.setDaysRemaining(currentDaysRemaining);
        }
    }
}
