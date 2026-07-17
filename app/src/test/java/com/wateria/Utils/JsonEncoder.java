package com.wateria.Utils;

import com.wateria.DataStructures.Plant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;

/** Frozen test fixture for the legacy preference format. Not part of the production runtime. */
public final class JsonEncoder {
    private JsonEncoder() {}

    public static String writePlantList(ArrayList<Plant> plants) {
        JSONArray array = new JSONArray();
        for (Plant plant : plants) {
            JSONObject object = new JSONObject();
            LocalDate date = plant.getNextWateringDate();
            try {
                object.put("name", plant.getPlantName());
                object.put("icon", plant.getIconId());
                object.put("day", date.getDayOfMonth());
                object.put("month", date.getMonthValue());
                object.put("year", date.getYear());
                object.put("wat_freq", plant.getWateringFrequency());
            } catch (JSONException ignored) {
                // This exactly preserves the legacy writer's best-effort behavior.
            }
            array.put(object);
        }
        return array.toString();
    }

    public static ArrayList<Plant> readPlantList(String value) {
        ArrayList<Plant> plants = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(value);
            for (int index = 0; index < array.length(); index++) {
                Plant plant = readPlant(array.optJSONObject(index));
                if (plant != null) {
                    plants.add(plant);
                }
            }
        } catch (JSONException ignored) {
            // This exactly preserves the legacy reader's empty-list fallback.
        }
        return plants;
    }

    private static Plant readPlant(JSONObject object) {
        try {
            return new Plant(
                    object.getString("name"),
                    object.getInt("icon"),
                    object.getInt("wat_freq"),
                    LocalDate.of(
                            object.getInt("year"),
                            object.getInt("month"),
                            object.getInt("day")
                    )
            );
        } catch (JSONException ignored) {
            return null;
        }
    }
}
