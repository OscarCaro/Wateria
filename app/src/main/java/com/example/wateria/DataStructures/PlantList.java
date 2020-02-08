package com.example.wateria.DataStructures;


import com.example.wateria.Activities.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

public class PlantList {

    private ArrayList<Plant> plantList;
    private final String sharedPrefPlantListKey = "plantlistkey";
    private SharedPreferences prefs;
    private Context appContext;
    private IconGenerator iconGenerator;    // Very costly to construct, so do it once and keep it in memory

    public PlantList(Context context){
        plantList = new ArrayList<Plant>();
        appContext = context.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public Plant get(int position){
        return plantList.get(position);
    }

    public void sort(){
        Collections.sort(plantList);
    }

    public void loadFromPrefs(){
        Gson gson = new Gson();
        String json = prefs.getString(sharedPrefPlantListKey, null);
        Type type = new TypeToken<ArrayList<Plant>>() {}.getType();
        plantList = gson.fromJson(json, type);

        setDaysRemaining();
        setIcons();
        sort();
    }

    public void saveToPrefs(){
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(plantList);
        editor.putString(sharedPrefPlantListKey, json);
        editor.apply();
    }

    public void setIcons(){
        if (iconGenerator == null ){
            iconGenerator = new IconGenerator(appContext);
        }
        for (Plant plant : plantList){
            plant.setIcon(iconGenerator.getDrawable(plant.getIconIdx()));
        }
    }

    public void setPlantIcon(int position){
        if (position >= 0 && position < plantList.size()){
            if (iconGenerator == null ){
                iconGenerator = new IconGenerator(appContext);
            }
            Plant p = plantList.get(position);
            p.setIcon(iconGenerator.getDrawable(p.getIconIdx()));
            plantList.set(position, p);
        }
        else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public Drawable getPlantIcon(int position){
        if (position >= 0 && position < plantList.size()){
            if (iconGenerator == null ){
                iconGenerator = new IconGenerator(appContext);
            }
            return iconGenerator.getDrawable(plantList.get(position).getIconIdx());
        }
        else {
            throw new ArrayIndexOutOfBoundsException();
        }
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

    public int getSize(){
        return plantList.size();
    }

//    public void fillWithPlants(){
//        String[] names_array = appContext.getResources().getStringArray(R.array.plantNames);
//
//        plantList.add(new Plant(names_array[0], 203, 6, LocalDate.of(2019,7,22)));
//
//        plantList.add(new Plant(names_array[1], 206, 5, LocalDate.of(2019,7,20)));
//
//        plantList.add(new Plant(names_array[2], 502, 12, LocalDate.of(2019,7,25)));
//
//        plantList.add(new Plant(names_array[3], 504, 3, LocalDate.of(2019,7,26)));
//
//        plantList.add(new Plant(names_array[4], 201, 8, LocalDate.of(2019,7,25)));
//
//        plantList.add(new Plant(names_array[5], 101, 4, LocalDate.of(2019,7,23)));
//
//        plantList.add(new Plant(names_array[6], 208, 3, LocalDate.of(2019,7,18)));
//
//        plantList.add(new Plant(names_array[7], 202, 9, LocalDate.of(2019,7,31)));
//
//        plantList.add(new Plant(names_array[8], 210, 6, LocalDate.of(2019,7,20)));
//    }

}
