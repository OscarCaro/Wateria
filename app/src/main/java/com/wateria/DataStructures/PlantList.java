package com.wateria.DataStructures;

import com.wateria.Utils.IconTagDecoder;
import com.wateria.Utils.JsonEncoder;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class PlantList {

    private static PlantList instance;

    private ArrayList<Plant> plantList;
    private static final String sharedPrefPlantListKey = "plantlistkey";
    private SharedPreferences prefs;
    private Context appContext;

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
        String json = prefs.getString(sharedPrefPlantListKey, null);
        if (json != null){
            plantList = JsonEncoder.readPlantList(json);

            setDaysRemaining();
            sort();
            setIcons();
        }
        else {
            plantList = new ArrayList<>();
        }
    }

    private void saveToPrefs(){
        String json = JsonEncoder.writePlantList(plantList);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(sharedPrefPlantListKey, json);
        editor.apply();
    }

    public void deleteAll(Context context){
        plantList.clear();
        saveToPrefs();
    }

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
