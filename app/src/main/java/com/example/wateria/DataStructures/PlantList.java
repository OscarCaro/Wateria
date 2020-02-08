package com.example.wateria.DataStructures;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PlantList {

    private ArrayList<Plant> plantList;
    private final String sharedPrefPlantListKey = "plantlistkey";
    private SharedPreferences prefs;

    public PlantList(Context context){
        plantList = new ArrayList<Plant>();
        prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public ArrayList<Plant> loadFromPrefs(){
        Gson gson = new Gson();
        String json = prefs.getString(sharedPrefPlantListKey, null);
        Type type = new TypeToken<ArrayList<Plant>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveToPrefs(){
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(plantList);
        editor.putString(sharedPrefPlantListKey, json);
        editor.apply();
    }

    public void setDaysRemaining (){
        LocalDate todayDate = LocalDate.now();
        int currentDaysRemaining;

        for (Plant plant : plantList){
            currentDaysRemaining = ((int) todayDate.until(plant.getNextWateringDate(), ChronoUnit.DAYS));
            if (currentDaysRemaining < 0){
                currentDaysRemaining = 0;
            }
            plant.setDaysRemaining(currentDaysRemaining);
        }
    }

    public int waterPlant (int position){
        Plant currentPlant = plantList.get(position);
        LocalDate date = LocalDate.now().plusDays(currentPlant.getWateringFrequency());
        currentPlant.setNextWateringDate(date);
        currentPlant.setDaysRemaining(currentPlant.getWateringFrequency());
        plantList.set(position, currentPlant);

        Collections.sort(plantList);

        return plantList.indexOf(currentPlant);
    }

    public int insertPlant (Plant plant){
        // Compute daysRemaining of new:
        plant.setDaysRemaining(((int) LocalDate.now().until(plant.getNextWateringDate(), ChronoUnit.DAYS)));
        // Insert to the list:
        plantList.add(plant);
        // Sort the list:
        Collections.sort(plantList);
        // Adapter:
        return plantList.indexOf(plant);
    }

    public int removePlant(int position) {
        if (position >= 0 && position < plantList.size()){
            plantList.remove(position);
            return position;
        }
        else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public int modifyPlant ( Plant plant, Integer prevPosInPlantList){
        if (prevPosInPlantList >= 0 && prevPosInPlantList < plantList.size()){
            // Compute daysRemaining of new:
            plant.setDaysRemaining(((int) LocalDate.now().until(plant.getNextWateringDate(), ChronoUnit.DAYS)));
            plantList.set(prevPosInPlantList, plant);
            Collections.sort(plantList);
            return plantList.indexOf(plant);
        }
        else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

}
