package com.example.wateria;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.wateria.DataStructures.Plant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class CheckPlantlistForNotificationService extends Service {

    private ArrayList<Plant> plantList = new ArrayList<Plant>();
    private SharedPreferences prefs;
    private String sharedPrefPlantListKey;

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
        checkPlantList(context);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void checkPlantList(Context context){

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefPlantListKey = context.getResources().getString(R.string.shared_prefs_plantlist_key);
        plantList = getArrayList();

        if (plantList.size() > 0){
            setDaysRemainingList(plantList);
            deleteNotZeroPlantsFromPlantlist(plantList);
            Collections.sort(plantList);

            if (plantList.size() > 0) {
                //After deleting the no-zero plants

                //Compute notifications
                NotificationClass.createNotificationChannel(context);
                NotificationClass.pushNotification(context, plantList);
            }
        }
    }

    public void deleteNotZeroPlantsFromPlantlist(ArrayList<Plant> plantList){
        for (int i = plantList.size() - 1; i >= 0; i--){
            if (plantList.get(i).getDaysRemaining() > 0){
                plantList.remove(i);
            }
        }
    }

    public ArrayList<Plant> getArrayList(){
        Gson gson = new Gson();
        String json = prefs.getString(sharedPrefPlantListKey, null);
        Type type = new TypeToken<ArrayList<Plant>>() {}.getType();
        return gson.fromJson(json, type);
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

