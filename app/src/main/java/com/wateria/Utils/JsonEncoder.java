package com.wateria.Utils;

import android.widget.Toast;

import com.wateria.DataStructures.Plant;
import com.wateria.DataStructures.PlantList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;

public class JsonEncoder {

    private static final String name_key = "name";
    private static final String icon_id_key = "icon";
    private static final String date_day_key = "day";
    private static final String date_month_key = "month";
    private static final String date_year_key = "year";
    private static final String wat_freq_key = "wat_freq";


    public static String writePlantList(ArrayList<Plant> arrayList){
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < arrayList.size(); i++){
            JSONObject object = new JSONObject();
            writePlant(arrayList.get(i), object);
            jsonArray.put(object);
        }
        return jsonArray.toString();
    }

    private static void writePlant(Plant plant, JSONObject object){
        LocalDate date = plant.getNextWateringDate();
        try {
            object.put(name_key, plant.getPlantName());
            object.put(icon_id_key, plant.getIconId());
            object.put(date_day_key, date.getDayOfMonth());
            object.put(date_month_key, date.getMonthValue());
            object.put(date_year_key, date.getYear());
            object.put(wat_freq_key, plant.getWateringFrequency());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Plant> readPlantList(String string){
        ArrayList<Plant> arrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(string);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.optJSONObject(i);
                Plant plant = readPlant(object);
                if(plant != null){
                    arrayList.add(plant);
                }
            }
            return arrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private static Plant readPlant(JSONObject object) {
        try {
            String plantName = object.getString(name_key);
            int iconId = object.getInt(icon_id_key);
            LocalDate nextWateringDate = LocalDate.of(object.getInt(date_year_key),  object.getInt(date_month_key), object.getInt(date_day_key));
            int wateringFrequency = object.getInt(wat_freq_key);
            return new Plant(plantName, iconId, wateringFrequency, nextWateringDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
