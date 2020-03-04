package com.example.wateria.DataStructures;

import com.example.wateria.Utils.IconTagDecoder;
import com.example.wateria.Utils.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private static final String sharedPrefPlantListKey = "plantlistkey";
    private SharedPreferences prefs;
    private Context appContext;
    //private IconGenerator iconGenerator;    // Very costly to construct, so do it once and keep it in memory

    public PlantList(Context context){
        plantList = new ArrayList<Plant>();
        appContext = context.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public static void deleteAll(Context context){
        // Only used from SettingsActivity when "Delete data" is pressed
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        prefs.edit().remove(sharedPrefPlantListKey).apply();
    }

    public Plant get(int position){
        return plantList.get(position);
    }

    public void sort(){
        Collections.sort(plantList);
    }

    public void loadFromPrefs(boolean loadIcons){
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe());     // For LocalDate internal attributes

        Gson gson = gb.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        String json = prefs.getString(sharedPrefPlantListKey, null);
        if (json != null){
            Type type = new TypeToken<ArrayList<Plant>>() {}.getType();
            plantList = gson.fromJson(json, type);

            setDaysRemaining();
            sort();
            if (loadIcons){
                setIcons();
            }
        }
        else {
            // Used in the case of:
            // Plantlist is filled. "Delete data" in settingsActivity is clicked. MainActivity.onResume() is called
            // Existing data must be erased from the list
            plantList.clear();
        }
    }

    public void saveToPrefs(){
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe());     // For LocalDate internal attributes

        Gson gson = gb.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();     // For Drawable attribute on Plant

        String json = gson.toJson(plantList);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(sharedPrefPlantListKey, json);
        editor.apply();
    }

    public void setIcons(){
        for (Plant plant : plantList){
            plant.setIcon(IconTagDecoder.idToDrawable(appContext, plant.getIconId()));
        }
    }

    public Drawable getPlantIcon(int position){
        if (plantList.get(position).getIcon() != null){
            // Already computed and stored in Plant
            return plantList.get(position).getIcon();
        }
        else{
            return IconTagDecoder.idToDrawable(appContext, plantList.get(position).getIconId());
        }
    }

    public void setDaysRemaining (){
        for (Plant plant : plantList){
            plant.computeDaysRemaining();
        }
    }

    public int waterPlant (int position){
        Plant currentPlant = plantList.get(position);
        currentPlant.water();
        plantList.set(position, currentPlant);

        Collections.sort(plantList);

        return plantList.indexOf(currentPlant);
    }

    public int find (Plant plant){
        return plantList.indexOf(plant);
    }

    public ArrayList<Plant> get0daysRemSublist (){
        ArrayList<Plant> sublist = new ArrayList<>();
        for(Plant plant : plantList){
            if (plant.getDaysRemaining()<= 0){
                sublist.add(plant);
            }
        }
        return sublist;
    }

    public int getDaysRemaining(int position){
        return plantList.get(position).getDaysRemaining();
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
}
