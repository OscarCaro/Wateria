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

    private static PlantList instance;

    private ArrayList<Plant> plantList;
    private static final String sharedPrefPlantListKey = "plantlistkey";
    private SharedPreferences prefs;
    private Context appContext;
    //private IconGenerator iconGenerator;    // Very costly to construct, so do it once and keep it in memory

    private PlantList(Context context){
        plantList = new ArrayList<>();
        appContext = context.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public static PlantList getInstance(Context context){
        if (instance == null){
            instance = new PlantList(context);
            instance.loadFromPrefs();
        }
        return instance;
    }

    private void loadFromPrefs(){
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe());     // For LocalDate internal attributes

        Gson gson = gb.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        String json = prefs.getString(sharedPrefPlantListKey, null);
        if (json != null){
            Type type = new TypeToken<ArrayList<Plant>>() {}.getType();
            plantList = gson.fromJson(json, type);

            setDaysRemaining();
            sort();
            setIcons();
        }
        else {
            plantList = new ArrayList<>();
        }
    }

    private void saveToPrefs(){
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe());     // For LocalDate internal attributes

        Gson gson = gb.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();     // For Drawable attribute on Plant

        String json = gson.toJson(plantList);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(sharedPrefPlantListKey, json);
        editor.apply();
    }

//    public static void deleteAll(Context context){
//        // Only used from SettingsActivity when "Delete data" is pressed
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
//        prefs.edit().remove(sharedPrefPlantListKey).apply();
//    }

    /**
     * Shouldn't modify the returned plant
     */
    public Plant get(int position){
        return plantList.get(position);
    }

    private void sort(){
        Collections.sort(plantList);
    }

    private void setIcons(){
        for (Plant plant : plantList){
            plant.setIcon(IconTagDecoder.idToDrawable(appContext, plant.getIconId()));
        }
    }

//    public Drawable getPlantIcon(int position){
//        if (plantList.get(position).getIcon() != null){
//            // Already computed and stored in Plant
//            return plantList.get(position).getIcon();
//        }
//        else{
//            return IconTagDecoder.idToDrawable(appContext, plantList.get(position).getIconId());
//        }
//    }

    private void setDaysRemaining (){
        for (Plant plant : plantList){
            plant.computeDaysRemaining();
        }
    }

    public int waterPlant (int position){
        Plant currentPlant = plantList.get(position);
        currentPlant.water();

        Collections.sort(plantList);

        saveToPrefs();

        return plantList.indexOf(currentPlant);
    }

    public int find (Plant plant){
        return plantList.indexOf(plant);
    }

    public boolean exists(String plantName){
        for(Plant plant : plantList){
            if (plant.getPlantName().equals(plantName)){
                return true;
            }
        }
        return false;
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

//    public int getDaysRemaining(int position){
//        return plantList.get(position).getDaysRemaining();
//    }

    public int insertPlant (Plant plant){
        // Compute daysRemaining of new:
        plant.setDaysRemaining(((int) LocalDate.now().until(plant.getNextWateringDate(), ChronoUnit.DAYS)));
        // Insert to the list:
        plantList.add(plant);
        // Sort the list:
        Collections.sort(plantList);
        //Save the list:
        saveToPrefs();
        // Adapter:
        return plantList.indexOf(plant);
    }

    public int removePlant(int position) {
        if (position >= 0 && position < plantList.size()){
            plantList.remove(position);
            saveToPrefs();
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
            saveToPrefs();
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
